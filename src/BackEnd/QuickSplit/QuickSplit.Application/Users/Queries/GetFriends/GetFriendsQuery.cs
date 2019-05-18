using System.Collections.Generic;
using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Queries.GetFriends
{
    public class GetFriendsQuery : IRequest<IEnumerable<UserModel>>

    {
    public int UserId { get; set; }
    }
}