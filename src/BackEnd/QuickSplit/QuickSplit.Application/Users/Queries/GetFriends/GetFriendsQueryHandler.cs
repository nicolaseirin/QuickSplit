using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries.GetFriends
{
    public class GetFriendsQueryHandler : IRequestHandler<GetFriendsQuery, IEnumerable<UserModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetFriendsQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetFriendsQuery request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId);
            
            if(user == null)
                throw new InvalidQueryException($"No existe usuario con id {request.UserId}");

            return user
                .Friends
                .Select(friendship => new UserModel(friendship.Friend1))
                .ToList();
        }
    }
}