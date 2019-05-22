using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class DeleteFriendCommandHandler : IRequestHandler<DeleteFriendCommand, Unit>
    {
        private readonly IQuickSplitContext _context;

        public DeleteFriendCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(DeleteFriendCommand request, CancellationToken cancellationToken)
        {
            User current = await _context.Users.FindAsync(request.CurrentUserId);
            User friend = await _context.Users.FindAsync(request.FriendUserId);
            
            if (current == null || friend == null)
                return Unit.Value;
            
            current.RemoveFriend(friend);
            await _context.SaveChangesAsync();
            
            return Unit.Value;
        }
    }
    
    public class DeleteFriendCommand : IRequest
    {
        public int CurrentUserId { get; set; }
        public int FriendUserId { get; set; }
    }
}