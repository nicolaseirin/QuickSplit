using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using QuickSplit.Domain;
using QuickSplit.Application.Memberships.Models;
using QuickSplit.Application.Membership.Queries.GetMemberships;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Memberships.Queries.GetMemberships
{
    public class GetMembershipsQueryHandler : IRequestHandler<GetMembershipsQuery, IEnumerable<MembershipModel>>
    {
        private IQuickSplitContext _context;

        public GetMembershipsQueryHandler(IQuickSplitContext context)
        {
            this._context = context;
        }

        public async Task<IEnumerable<MembershipModel>> Handle(GetMembershipsQuery request, CancellationToken cancellationToken)
        {
            return await _context
                .Memberships
                .Select(mem => MapToModel(mem))
                .ToListAsync();
        }

        private MembershipModel MapToModel(Domain.Membership membership)
        {
            return new MembershipModel()
            {
                UserId = membership.UserId,
                GroupId = membership.GroupId,
                UserModel = new UserModel(membership.User),
                GroupModel = new GroupModel(membership.Group),
            };
        }
    }
}


