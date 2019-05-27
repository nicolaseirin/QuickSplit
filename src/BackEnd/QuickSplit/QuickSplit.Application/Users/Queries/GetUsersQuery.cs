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
        private readonly IAvatarRepository _avatarRepository;

        public GetUsersQueryHandler(IQuickSplitContext context, IAvatarRepository avatarRepository)
        {
            this._context = context;
            _avatarRepository = avatarRepository;
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

            return await Task.WhenAll(users.Select(MapToModel));
        }

        private async Task<UserModel> MapToModel(User user)
        {
            string avatar = await _avatarRepository.GetAvatarBase64(user.Id);

            return new UserModel(user, avatar);
        }
    }
    
    public class GetUsersQuery : IRequest<IEnumerable<UserModel>>
    {
        public string SearchNameQuery { get; set; }
        
        public int? NotFriendWithQuery { get; set; }
        
    }
}