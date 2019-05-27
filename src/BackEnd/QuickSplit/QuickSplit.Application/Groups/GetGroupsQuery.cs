using System.Collections.Generic;
using MediatR;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Groups
{
    public class GetGroupsQuery: IRequest<IEnumerable<GroupModel>>
    {
    }
}

