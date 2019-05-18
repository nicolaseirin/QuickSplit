using System.Collections.Generic;
using System.Data;
using System.Linq;
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
            IEnumerable<Friendship> friendshipsToDelete = _context.Friendships.Where(friendship => friendship.Friend1Id == toDelete.Id || friendship.Friend2Id == toDelete.Id);
            
            if (toDelete != null)
            {
                _context.Friendships.RemoveRange(friendshipsToDelete);
                _context.Users.Remove(toDelete);
                await _context.SaveChangesAsync();
            }

            return Unit.Value;
        }
    }
}