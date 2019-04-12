using System;
using System.Collections.Generic;
using System.Text;
using MediatR;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Groups.Commands.CreateGroup
{
    public class CreateGroupCommand: IRequest<GroupModel>
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public string Admin { get; set; }
    }
}

