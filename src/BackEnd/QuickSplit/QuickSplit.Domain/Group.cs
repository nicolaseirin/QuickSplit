using System;
using System.Collections.Generic;
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

        public Group()
        {
            Memberships = new List<Membership>();
        }

        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if (string.IsNullOrWhiteSpace(value))
                throw new DomainException($"{propertyName} is required");
        }
    }
}
