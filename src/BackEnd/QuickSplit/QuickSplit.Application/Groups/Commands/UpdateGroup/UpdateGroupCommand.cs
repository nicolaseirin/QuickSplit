using MediatR;
using QuickSplit.Application.Groups.Models;
using System.Collections.Generic;

namespace QuickSplit.Application.Groups.Commands.UpdateGroup
{
    public class UpdateGroupCommand: IRequest<GroupModel>
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int Admin { get; set; }

        public ICollection<int> Memberships { get; set; }

    }
}
