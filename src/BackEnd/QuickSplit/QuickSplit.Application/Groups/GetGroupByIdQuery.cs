using MediatR;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Groups
{
    public class GetGroupByIdQuery: IRequest<GroupModel>
    {
        public int Id { get; set; }
    }
}



