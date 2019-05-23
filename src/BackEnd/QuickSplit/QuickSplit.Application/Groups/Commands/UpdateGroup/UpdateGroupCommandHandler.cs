using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands.UpdateGroup
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
            Domain.Membership[] memberships = await Task.WhenAll(request.Memberships.Select(i => GetMemberships(i, toCreate)));
            toUpdate.Memberships = memberships;

            await _context.SaveChangesAsync();

            return new GroupModel(toUpdate);
        }

        private async Task<Domain.Membership> GetMemberships(int userId, Group group)
        {
            User user = await _context.Users.FindAsync(userId) ?? throw new InvalidCommandException($"Miembro del grupo con id {userId} no existe"); ;

            return new Domain.Membership()
            {
                User = user,
                UserId = user.Id,
                Group = group,
                GroupId = group.Id
            };
        }
    }
}
