using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands
{
    public class DeleteGroupCommandHandler: IRequestHandler<DeleteGroupCommand>
    {
        private readonly IQuickSplitContext _context;

        public DeleteGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(DeleteGroupCommand request, CancellationToken cancellationToken)
        {
            Group toDelete = await _context
                .Groups
                .FirstOrDefaultAsync(g => g.Id == request.Id, cancellationToken: cancellationToken);
            if (toDelete == null)
                throw new InvalidCommandException($"No existe el grupo con id {request.Id}");

            DeleteMemberships(toDelete);
            _context.Groups.Remove(toDelete);
            _context.Purchases.RemoveRange(toDelete.Purchases);
            
            await _context.SaveChangesAsync();

            return Unit.Value;
        }

        private async void DeleteMemberships(Group toDelete)
        {
            var memberships = toDelete.Memberships;
            foreach (var mem in memberships)
            {
               _context.Memberships.Remove(mem);
            }
            _context.SaveChanges();
        }
    }
    
    public class DeleteGroupCommand: IRequest
    {
        public int Id { get; set; }
    }
}