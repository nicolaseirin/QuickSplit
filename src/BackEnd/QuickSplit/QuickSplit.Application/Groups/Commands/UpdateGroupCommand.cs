﻿using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands
{
    public class UpdateGroupCommandHandler : IRequestHandler<UpdateGroupCommand, GroupModel>
    {
        private readonly IQuickSplitContext _context;

        public UpdateGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<GroupModel> Handle(UpdateGroupCommand request, CancellationToken cancellationToken)
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

        private async Task<GroupModel> TryToUpdate(UpdateGroupCommand request)
        {
            int id = request.Id;
            Group toUpdate = await _context.Groups.FindAsync(id);
            toUpdate.Name = request.Name ?? toUpdate.Name;
            CleanMemberships(toUpdate);
            Domain.Membership[] memberships = await Task.WhenAll(request.Memberships.Select(i => GetMemberships(i, toUpdate)));
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
            ;

            return new Domain.Membership()
            {
                User = user,
                UserId = user.Id,
                Group = group,
                GroupId = group.Id
            };
        }
    }

    public class UpdateGroupCommand : IRequest<GroupModel>
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public ICollection<int> Memberships { get; set; }
    }
}