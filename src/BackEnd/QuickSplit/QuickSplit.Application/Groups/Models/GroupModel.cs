using System.Collections;
using QuickSplit.Domain;
using System.Collections.Generic;
using System.Linq;

namespace QuickSplit.Application.Groups.Models
{
    public class GroupModel
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int Admin { get; set; }

        public ICollection<int> Memberships { get; set; }
        
        public IEnumerable<int> Purchases { get; set; } 

        public GroupModel()
        {
            Memberships = new List<int>(); 
        }

        public GroupModel(Group group)
        {
            Id = group.Id;
            Name = group.Name;
            var a = group.Admin;
            Admin = group.Admin.Id;
            Memberships = new List<int>();
            SetMemberships(group.Memberships);
            Purchases = group.Purchases.Select(purchase => purchase.Id);
        }

        private void SetMemberships(ICollection<Domain.Membership> memberships)
        {
            foreach (Domain.Membership m in memberships)
            {
                Memberships.Add(m.UserId);
            }
        }
    }
}
