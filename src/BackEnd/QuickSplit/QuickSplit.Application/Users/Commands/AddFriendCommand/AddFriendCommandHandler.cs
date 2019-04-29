using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Commands.UpdateUser;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.AddFriendCommand
{
    public class AddFriendCommandHandler : IRequestHandler<AddFriendCommand, Unit>
    {
        private readonly IQuickSplitContext _context;

        public AddFriendCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(AddFriendCommand request, CancellationToken cancellationToken)
        {
            User current = await _context.Users.FindAsync(request.CurrentUserId);
            User toAdd = await _context.Users.FindAsync(request.FriendUserId);
            
            current.AddFriend(toAdd);

            await _context.SaveChangesAsync();
            return Unit.Value;
        }
    }
}