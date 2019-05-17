using System;
using System.Collections.Generic;
using System.Text;
using MediatR;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands.CreateGroup
{
    public class CreateGroupCommand : IRequest<GroupModel>
    {
        public string Name { get; set; }

        public int Admin { get; set; }

        public ICollection<int> Memberships {get; set;}
    }
}

