using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace QuickSplit.Domain
{
    public class Group
    {
        private string name;

        public virtual ICollection<Membership> Memberships { get; set; } = new List<Membership>();
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

        public virtual User Admin { get; set; }

        public virtual ICollection<Purchase> Purchases { get; set; } = new List<Purchase>();
        
        public Group()
        {
            Memberships = new List<Membership>();
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

        public SplitCostReport GenerateSplitCostReport(Currency currency)
        {
            return new SplitCostReport(this, currency);
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
