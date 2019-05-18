using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore.Query.Internal;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Commands.UpdateUser;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.AddFriendCommand
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
}