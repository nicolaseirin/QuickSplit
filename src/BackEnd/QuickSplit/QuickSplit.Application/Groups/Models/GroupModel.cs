using QuickSplit.Domain;
using System.Collections.Generic;

namespace QuickSplit.Application.Groups.Models
{
    public class GroupModel
    {
        public GroupModel()
        {
            Memberships = new List<int>(); 
        }

        public GroupModel(Group group)
        {
            Id = group.Id;
            Name = group.Name;
            Admin = group.Admin;
            GetMemberships(group.Memberships);
        }

        public int Id { get; set; }

        public string Name { get; set; }

        public int Admin { get; set; }

        public ICollection<int> Memberships { get; set; }

        private void GetMemberships(ICollection<Domain.Membership> memberships)
        {
            foreach (Domain.Membership m in memberships)
            {
                Memberships.Add(m.GroupId);
            }
        }

    }
}
