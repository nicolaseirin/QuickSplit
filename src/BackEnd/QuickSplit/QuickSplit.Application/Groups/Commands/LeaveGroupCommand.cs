using MediatR;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Groups.Commands
{
    public class LeaveGroupCommand: IRequest<GroupModel>
    {
        public int GroupId { get; set; }
        public int UserId { get; set; }
    }
}


