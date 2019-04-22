using MediatR;

namespace QuickSplit.Application.Users.Queries.GetPassword
{
    public class PasswordIsValidQuery : IRequest<bool>
    {
        public string Mail { get; set; }
        
        public string Password { get; set; }
    }
}