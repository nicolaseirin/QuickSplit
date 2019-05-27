﻿using System;
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