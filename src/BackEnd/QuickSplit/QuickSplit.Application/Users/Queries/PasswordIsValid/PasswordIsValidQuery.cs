using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Queries.GetPassword
{
    public class PasswordIsValidQuery : IRequest<UserModel>
    {
        public string Mail { get; set; }
        
        public string Password { get; set; }
    }
}