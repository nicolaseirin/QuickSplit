using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.Application.Users.Queries
{
    public class GetGroupsQueryHandler : IRequestHandler<GetGroupsQuery, IEnumerable<GroupModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetGroupsQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<GroupModel>> Handle(GetGroupsQuery request, CancellationToken cancellationToken)
        {
            var mems = await _context
                .Memberships
                .Where(membership => membership.UserId == request.UserId)
                .ToListAsync(cancellationToken);

            return mems.Select(membership => new GroupModel(membership.Group));
        }
    }

    public class GetGroupsQuery : IRequest<IEnumerable<GroupModel>>
    {
        public int UserId { get; set; }
    }
}