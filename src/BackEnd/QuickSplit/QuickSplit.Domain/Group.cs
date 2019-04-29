using System;
using System.Collections.Generic;
using System.Text;

namespace QuickSplit.Domain
{
    public class Group
    {
        private string name;
        private int admin;

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

        public int Admin
        {
            get => admin;
            set
            {
                admin = value;
            }
        }

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
