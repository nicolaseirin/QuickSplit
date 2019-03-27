using MediatR;

namespace QuickSplit.Application.Users.Commands.CreateUser
{
    public class CreateUserCommand : IRequest
    {     
        public string Name { get; set; }
        
        public string LastName { get; set; }
        
        public string Mail { get; set; }
        
        public string Telephone { get; set; }
        
        public string Password { get; set; }
    }
}