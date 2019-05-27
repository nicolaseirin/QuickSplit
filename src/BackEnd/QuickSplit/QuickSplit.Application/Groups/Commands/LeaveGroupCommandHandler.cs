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
    public class LeaveGroupCommandHandler: IRequestHandler<LeaveGroupCommand, Unit >
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
            Group group = await _context.Groups.FindAsync(request.GroupId) ?? throw new InvalidCommandException($"El grupo con id {request.GroupId} no existe");
            Domain.Membership membership =  await _context.Memberships.FirstOrDefaultAsync(m=> m.Group == group && m.User == user) ;
           
            group.Memberships.Remove(membership);
            _context.Memberships.Remove(membership);         
            
            await _context.SaveChangesAsync();

            return Unit.Value;
        }
    }
}


