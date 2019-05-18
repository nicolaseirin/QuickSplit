using MediatR;

namespace QuickSplit.Application.Users.Commands.AddFriendCommand
{
    public class AddFriendCommand : IRequest
    {
        public int CurrentUserId { get; set; }
        
        public int FriendUserId { get; set; }
    }
}