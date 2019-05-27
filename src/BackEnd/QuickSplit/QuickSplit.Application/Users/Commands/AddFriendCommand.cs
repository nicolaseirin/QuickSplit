using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class AddFriendCommandHandler : IRequestHandler<AddFriendCommand>
    {
        private readonly IQuickSplitContext _context;

        public AddFriendCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(AddFriendCommand request, CancellationToken cancellationToken)
        {
            Task<User> current = _context.Users.FindAsync(request.CurrentUserId);
            Task<User> toAdd = _context.Users.FindAsync(request.FriendUserId);
            User currentUser = await current;
            User userToAdd = await toAdd;
            
            if(currentUser == null || userToAdd == null)
                throw new InvalidCommandException("Usuarios no existen");
            
            currentUser.AddFriend(userToAdd);

            await _context.SaveChangesAsync();
            return Unit.Value;
        }
    }
    
    public class AddFriendCommand : IRequest
    {
        public int CurrentUserId { get; set; }
        
        public int FriendUserId { get; set; }
    }
}