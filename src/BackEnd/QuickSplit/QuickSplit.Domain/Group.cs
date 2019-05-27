using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QuickSplit.Domain
{
    public class Group
    {
        private string name;

        public ICollection<Membership> Memberships { get; set; }
        public int Id { get; set; }

        public string Name
        {
            get => name;
            set
            {
                ValidateNotNullOrEmpty(value, "Name");
                name = value;
            }
        }

        public User Admin { get; set; }

        public ICollection<Purchase> Purchases { get; set; }
        
        public Group()
        {
            Memberships = new List<Membership>();
            Purchases = new List<Purchase>();
        }
        

        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if (string.IsNullOrWhiteSpace(value))
                throw new DomainException($"{propertyName} is required");
        }

        public bool UserIsPartOfGroup(User user)
        {
            return Memberships.Any(membership => membership.UserId == user.Id);
        }
        
        protected bool Equals(Group other)
        {
            return Id == other.Id;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Group) obj);
        }

        public override int GetHashCode()
        {
            return Id;
        }
    }
}
