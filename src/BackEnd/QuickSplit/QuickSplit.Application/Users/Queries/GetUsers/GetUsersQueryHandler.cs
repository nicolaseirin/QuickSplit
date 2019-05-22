using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries.GetUsers
{
    public class GetUsersQueryHandler : IRequestHandler<GetUsersQuery, IEnumerable<UserModel>>
    {
        private IQuickSplitContext context;

        public GetUsersQueryHandler(IQuickSplitContext context)
        {
            this.context = context;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetUsersQuery request, CancellationToken cancellationToken)
        {
            IQueryable<User> query = context.Users;

            if (!string.IsNullOrWhiteSpace(request.SearchNameQuery))
                query = query.Where(user => (user.Name + " " + user.LastName).Contains(request.SearchNameQuery, StringComparison.OrdinalIgnoreCase));

            
            
            if (request.NotFriendWithQuery != null)
                query = query
                    .Where(user => user.Id != request.NotFriendWithQuery)
                    .Where(user => user.FriendsOf.All(friendship => friendship.Friend2Id != request.NotFriendWithQuery));

            

            return await query
                .Select(user => MapToModel(user))
                .ToListAsync(cancellationToken);
        }

        private UserModel MapToModel(User user)
        {
            return new UserModel()
            {
                Id = user.Id,
                Name = user.Name,
                LastName = user.LastName,
                Mail = user.Mail
            };
        }
    }
}