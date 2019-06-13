using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
{
    public class GetUsersQueryHandler : IRequestHandler<GetUsersQuery, IEnumerable<UserModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetUsersQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            this._context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetUsersQuery request, CancellationToken cancellationToken)
        {
            IQueryable<User> query = _context.Users;

            if (!string.IsNullOrWhiteSpace(request.SearchNameQuery))
                query = query.Where(user => (user.Name + " " + user.LastName).Contains(request.SearchNameQuery, StringComparison.OrdinalIgnoreCase));

            if (request.NotFriendWithQuery != null)
                query = query
                    .Where(user => user.Id != request.NotFriendWithQuery)
                    .Where(user => user.FriendsOf.All(friendship => friendship.Friend2Id != request.NotFriendWithQuery));

            List<User> users = await query.ToListAsync(cancellationToken);

            return users.Select(u => new UserModel(u));
        }
    }

    public class GetUsersQuery : IRequest<IEnumerable<UserModel>>
    {
        public string SearchNameQuery { get; set; }

        public int? NotFriendWithQuery { get; set; }
    }
}