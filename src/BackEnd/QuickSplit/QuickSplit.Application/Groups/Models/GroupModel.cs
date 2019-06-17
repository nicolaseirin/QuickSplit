using System.Collections;
using QuickSplit.Domain;
using System.Collections.Generic;
using System.Linq;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.Application.Groups.Models
{
    public class GroupModel
    {
        public int Id { get; set; }

        public string Name { get; set; }

        public int Admin { get; set; }

        public ICollection<UserModel> Memberships { get; set; }
        
        public IEnumerable<int> Purchases { get; set; } 

        public GroupModel()
        {
            Memberships = new List<UserModel>(); 
        }

        public GroupModel(Group group)
        {
            Id = group.Id;
            Name = group.Name;
            Admin = group.Admin.Id;
            Memberships = new List<UserModel>();
            SetMemberships(group.Memberships);
            Purchases = group.Purchases.Select(purchase => purchase.Id);
        }

        private void SetMemberships(ICollection<Domain.Membership> memberships)
        {
            foreach (Domain.Membership m in memberships)
            {
                Memberships.Add(new UserModel(m.User));
            }
        }
    }
}
