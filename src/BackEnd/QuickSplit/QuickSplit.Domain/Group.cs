using System;
using System.Collections.Generic;
using System.Text;

namespace QuickSplit.Domain
{
    public class Group
    {
        private string name;
        private string admin;

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

        public string Admin
        {
            get => admin;
            set
            {
                ValidateNotNullOrEmpty(value, "Administrator");
                name = value;
            }
        }

        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if (string.IsNullOrWhiteSpace(value))
                throw new DomainException($"{propertyName} is required");
        }
    }
}
