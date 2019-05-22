using System.Collections.Generic;
using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Queries.GetUsers
{
    public class GetUsersQuery : IRequest<IEnumerable<UserModel>>
    {
        public string SearchNameQuery { get; set; }
        
        public int? NotFriendWithQuery { get; set; }
        
    }
}