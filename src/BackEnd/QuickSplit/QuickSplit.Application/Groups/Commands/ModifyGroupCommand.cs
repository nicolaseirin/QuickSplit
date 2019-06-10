using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Internal;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands
{
    public class UpdateGroupCommandHandler : IRequestHandler<ModifyGroupCommand, GroupModel>
    {
        private readonly IQuickSplitContext _context;

        public UpdateGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<GroupModel> Handle(ModifyGroupCommand request, CancellationToken cancellationToken)
        {
            try
            {
                return await TryToUpdate(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }
        }

        private async Task<GroupModel> TryToUpdate(ModifyGroupCommand request)
        {
            int id = request.Id;
            Group toUpdate = await _context.Groups
                .Include(group => group.Memberships)
                .Include(group => group.Admin)
                .Include(group => group.Purchases)
                .FirstOrDefaultAsync(g => g.Id == id);
            toUpdate.Name = request.Name ?? toUpdate.Name;
            
            var oldUsers = toUpdate.Memberships.Select(membership => membership.UserId).ToList();
            CleanMemberships(toUpdate);
            Domain.Membership[] memberships = await Task.WhenAll(request.Memberships.Select(i => GetMemberships(i, toUpdate)));
            var newUsers = memberships.Select(membership => membership.UserId).ToList();
            var toRemove = oldUsers.Except(newUsers).ToList();
            foreach (Purchase purchase in toUpdate.Purchases.ToList())
            {
                foreach (int i in toRemove)
                {
                    purchase.RemoveParticipant(new User() {Id = i});
                }

                if (toRemove.Contains(purchase.Purchaser.Id))
                    toUpdate.Purchases.Remove(purchase);
            }

            
            
            toUpdate.Memberships = memberships;

            await _context.SaveChangesAsync();

            return new GroupModel(toUpdate);
        }

        private void CleanMemberships(Group toUpdate)
        {
            var memberships = toUpdate.Memberships;
            foreach (var mem in memberships)
            {
                _context.Memberships.Remove(mem);
            }

            _context.SaveChanges();
        }


        private async Task<Domain.Membership> GetMemberships(int userId, Group group)
        {
            User user = await _context.Users.FindAsync(userId) ?? throw new InvalidCommandException($"Miembro del grupo con id {userId} no existe");

            Domain.Membership membership = await _context.Memberships.FirstOrDefaultAsync(mem => mem.UserId == user.Id && group.Id == mem.GroupId);

            return membership
                   ?? new Domain.Membership()
                   {
                       User = user,
                       UserId = user.Id,
                       Group = group,
                       GroupId = group.Id
                   };
        }
    }

    public class ModifyGroupCommand : IRequest<GroupModel>
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public ICollection<int> Memberships { get; set; }
    }
}