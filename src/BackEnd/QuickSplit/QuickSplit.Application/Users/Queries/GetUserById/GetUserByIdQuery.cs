using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Queries.GetUserById
{
    public class GetUserByIdQuery : IRequest<UserModel>
    {
        public int Id { get; set; }
    }
}