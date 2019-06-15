using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
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
            /*
            User current = await _context.Users.Include(u => u.Friends).Include(u => u.FriendsOf).FirstOrDefaultAsync(u => u.Id == request.CurrentUserId, cancellationToken: cancellationToken);
            User friend = await _context.Users.Include(u => u.Friends).Include(u => u.FriendsOf).FirstOrDefaultAsync(u => u.Id == request.FriendUserId, cancellationToken: cancellationToken);
            
            if (current == null || friend == null)
                return Unit.Value;
            
            current.RemoveFriend(friend);
            */
            var friendshipsToRemove = await _context.Friendships.Where(friendship => (friendship.Friend1Id == request.CurrentUserId && friendship.Friend2Id == request.FriendUserId)
                                                                                     || (friendship.Friend1Id == request.FriendUserId && friendship.Friend2Id == request.CurrentUserId))
                .ToListAsync(cancellationToken: cancellationToken);
            _context.Friendships.RemoveRange(friendshipsToRemove);

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