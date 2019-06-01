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
            IEnumerable<Domain.Membership> memberships = await _context
                              .Memberships
                              .Include(mem => mem.Group)
                              .ThenInclude(group => group.Purchases)
                              .ThenInclude(mem => mem.Group)
                              .ThenInclude(admin => admin.Admin)
                              .Where(mem => mem.UserId == request.Id)
                              .ToListAsync();
                            
            return await Task.WhenAll(memberships.Select(m => MapToModel(m.Group)));
        }

        private async Task<GroupModel> MapToModel(Group group)
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
                groupModel.Memberships = group.Memberships.Select(g => g.UserId).ToList();
            }

            return groupModel;
        }
    }
    
    public class GetGroupsQuery: IRequest<IEnumerable<GroupModel>>
    {
        public int Id { get; set; }
    }
}