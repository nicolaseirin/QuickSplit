using System.Collections.Generic;
using MediatR;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Users.Queries.GetUsers
{
    public class GetUsersQuery : IRequest< IEnumerable<UserModel>>
    {
        
    }
}