using System.Data;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.DeleteUser
{
    public class DeleteUserCommandHandler : IRequestHandler<DeleteUserCommand>
    {
        private readonly IQuickSplitContext _context;

        public DeleteUserCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(DeleteUserCommand request, CancellationToken cancellationToken)
        {
            User toDelete = await _context.Users.FindAsync(request.Id);   
            if(toDelete == null)
                throw new InvalidCommandException($"Not existe el usuario con id {request.Id}");
            
            _context.Users.Remove(toDelete);
            await _context.SaveChangesAsync();

            return Unit.Value;
        }
    }
}