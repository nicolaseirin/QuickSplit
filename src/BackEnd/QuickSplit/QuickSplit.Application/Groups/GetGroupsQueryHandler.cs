using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups
{
    public class GetGroupsQueryHandler: IRequestHandler<GetGroupsQuery, IEnumerable<GroupModel>>
    {
        private IQuickSplitContext _context;

        public GetGroupsQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<GroupModel>> Handle(GetGroupsQuery request, CancellationToken cancellationToken)
        {
            return await _context
                .Groups
                .Include("Admin")
                .Include("Memberships")
                .Select(group => MapToModel(group))
                .ToListAsync();
        }

        private GroupModel MapToModel(Group group)
        {
            GroupModel groupModel = new GroupModel();
            groupModel.Id = group.Id;
            groupModel.Name = group.Name;
            groupModel.Admin = group.Admin.Id;
            if (group.Memberships.Count != 0)
            {
                groupModel.Memberships = group.Memberships.Select(g => g.UserId).ToList();   
            }
            return groupModel;
        }
    }
}
