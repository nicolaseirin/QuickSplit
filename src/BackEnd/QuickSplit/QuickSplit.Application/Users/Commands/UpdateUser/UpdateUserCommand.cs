using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Commands.UpdateUser
{
    public class UpdateUserCommand : IRequest<UserModel>
    {
        public string Name { get; set; }
        
        public int Id { get; set; }

        public string LastName { get; set; }
        
        public string Mail { get; set; }
        
        public string Password { get; set; }   
    }
}