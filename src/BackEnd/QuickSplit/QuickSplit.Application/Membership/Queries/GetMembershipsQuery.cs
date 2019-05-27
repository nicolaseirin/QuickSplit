using MediatR;
using QuickSplit.Application.Memberships.Models;
using System;
using System.Collections.Generic;
using System.Text;

namespace QuickSplit.Application.Membership.Queries.GetMemberships
{
    public class GetMembershipsQuery: IRequest<IEnumerable<MembershipModel>>
    {
        public GetMembershipsQuery()
        {
        }
    }
}


