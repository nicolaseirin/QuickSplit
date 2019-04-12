using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Commands.CreateUser
{
    public class CreateUserCommand : IRequest<UserModel>
    {
        public string Name { get; set; }

        public string LastName { get; set; } = "";
        
        public string Mail { get; set; }
        
        public string Password { get; set; }
    }
}