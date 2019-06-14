using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
{
    public class GetUserGroupsQueryHandler : IRequestHandler<GetUserGroupsQuery, IEnumerable<GroupModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetUserGroupsQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<GroupModel>> Handle(GetUserGroupsQuery request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FirstOrDefaultAsync(u => u.Id == request.UserId, cancellationToken: cancellationToken) ?? throw new InvalidQueryException("Usuario no existe");

            var memberships = await _context
                .Memberships
                .Where(membership => membership.UserId == user.Id)
                .Include(membership => membership.Group)
                .ThenInclude(g => g.Admin)
                .Include(membership => membership.Group)
                .ThenInclude(m => m.Memberships)
                .Include(membership => membership.Group)
                .ThenInclude(m => m.Purchases)
                .ToListAsync(cancellationToken: cancellationToken);

            return memberships
                .Select(m => new GroupModel(m.Group));
        }
    }
    
    public class GetUserGroupsQuery : IRequest<IEnumerable<GroupModel>>
    {
        public int UserId { get; set; }
    }
}