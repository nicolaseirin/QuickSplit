using System;
using System.Collections.Generic;
using System.Text;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Models
{
    public class GroupModel
    {
        public GroupModel()
        {
        }

        public GroupModel(Group group)
        {
            Id = group.Id;
            Name = group.Name;
            Admin = group.Admin;
        }

        public int Id { get; set; }

        public string Name { get; set; }

        public string Admin { get; set; }
    }
}
