using MediatR;

namespace QuickSplit.Application.Users.Queries.GetPassword
{
    public class PasswordIsValidQuery : IRequest<bool>
    {
        public int Id { get; set; }
        
        public string Password { get; set; }
    }
}