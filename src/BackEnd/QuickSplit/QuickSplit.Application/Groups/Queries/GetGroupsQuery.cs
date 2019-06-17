using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
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
            return await _context.Groups
                .Include(group => group.Admin)
                .Include(group => group.Purchases)
                .Include(group => group.Memberships)
                .Select(group => MapToModel(group))
                .ToListAsync(cancellationToken: cancellationToken);
        }

        private GroupModel MapToModel(Group group)
        {
            var groupModel = new GroupModel
            {
                Id = @group.Id,
                Name = @group.Name,
                Admin = @group.Admin.Id,
                Purchases = group.Purchases.Select(purchase => purchase.Id).ToList()
            };
            if (group.Memberships.Count != 0)
            {
                groupModel.Memberships = group.Memberships.Select(g => new UserModel(g.User)).ToList();
            }

            return groupModel;
        }
    }
    
    public class GetGroupsQuery: IRequest<IEnumerable<GroupModel>>
    {
        public int Id { get; set; }
    }
}