using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
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
            User user = await _context
                .Users
                .FindAsync(request.UserId);
            
            if (user == null)
                throw new InvalidQueryException($"No existe usuario con id {request.UserId}");

            return await _context.Friendships
                .Where(friendship => friendship.Friend2Id == request.UserId)
                .Select(friendship => new UserModel(friendship.Friend1))
                .ToListAsync(cancellationToken: cancellationToken);
        }
    }

    public class GetFriendsQuery : IRequest<IEnumerable<UserModel>>

    {
        public int UserId { get; set; }
    }
}