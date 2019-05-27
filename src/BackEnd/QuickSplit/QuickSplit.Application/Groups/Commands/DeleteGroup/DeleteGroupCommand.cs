using MediatR;

namespace QuickSplit.Application.Groups.Commands.DeleteGroup
{
    public class DeleteGroupCommand: IRequest
    {
        public int Id { get; set; }
    }
}
