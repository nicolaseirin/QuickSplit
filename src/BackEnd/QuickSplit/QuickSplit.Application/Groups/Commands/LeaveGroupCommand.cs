using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;
using System.Linq;
using Microsoft.EntityFrameworkCore;

namespace QuickSplit.Application.Groups.Commands
{
    public class LeaveGroupCommandHandler : IRequestHandler<LeaveGroupCommand, Unit>
    {
        private readonly IQuickSplitContext _context;

        public LeaveGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(LeaveGroupCommand request, CancellationToken cancellationToken)
        {
            try
            {
                await TryToHandle(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }

            return Unit.Value;
        }

        private async Task<Unit> TryToHandle(LeaveGroupCommand request)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidCommandException($"El usuario con id {request.UserId} no existe");
            Group group = await _context.Groups.Include(group1 => group1.Admin).FirstOrDefaultAsync(g => g.Id == request.GroupId) ?? throw new InvalidCommandException($"El grupo con id {request.GroupId} no existe");
            if (group.Admin.Id == request.UserId) throw new InvalidCommandException("El administrador no puede irse del grupo");
            Domain.Membership membership = await _context.Memberships.FirstOrDefaultAsync(m => m.Group.Id == group.Id && m.User.Id == user.Id) ?? throw new InvalidCommandException($"Usuario no es parte del grupo");

            group.Memberships.Remove(membership);
            _context.Memberships.Remove(membership);

            var toDelete = _context.Participants.Where(p => p.Purchase.Group.Id == group.Id && p.UserId == user.Id);
            _context.Participants.RemoveRange(toDelete);

            var cantLeave = _context.Purchases.Any(p => p.Purchaser.Id == user.Id && p.Group.Id == group.Id);
            if (cantLeave)
                throw new InvalidCommandException("No te podes ir del grupo si compraste algo");

            await _context.SaveChangesAsync();

            return Unit.Value;
        }
    }

    public class LeaveGroupCommand : IRequest
    {
        public int GroupId { get; set; }
        public int UserId { get; set; }
    }
}