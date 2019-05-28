using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace QuickSplit.Domain
{
    public class User
    {
        private string _name;
        private string _lastName;
        private string _mail;
        private string _password;

        public int Id { get; set; }

        public string Name
        {
            get => _name;
            set
            {
                ValidateNotNullOrEmpty(value, "Name");
                _name = value;
            }
        }

        public string LastName
        {
            get => _lastName;
            set => _lastName = value ?? throw new DomainException($"LastName is required");
        }

        public string Mail
        {
            get => _mail;
            set
            {
                IsValidMailString(value, "Mail");
                _mail = value;
            }
        }

        public string Password
        {
            get => _password;
            set
            {
                ValidateNotNullOrEmpty(value, "Password");
                _password = value;
            }
        }

        private void IsValidMailString(string value, string propertyName)
        {
            ValidateNotNullOrEmpty(value, propertyName);
            var isValidMail = new Regex(@"^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$");
            if (!isValidMail.IsMatch(value))
                throw new DomainException($"{value} is not a valid email address");
        }

        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if (string.IsNullOrWhiteSpace(value))
                throw new DomainException($"{propertyName} is required");
        }

        public virtual ICollection<Friendship> Friends { get; set; } = new List<Friendship>();
        public virtual ICollection<Friendship> FriendsOf { get; set; } = new List<Friendship>();

        public void AddFriend(User user)
        {
            var f1 = new Friendship(this, user);
            var f2 = new Friendship(user, this);
            
            FriendsOf.Add(f1);
            user.Friends.Add(f1);
            
            Friends.Add(f2);
            user.FriendsOf.Add(f2);
        }

        public void RemoveFriend(User user)
        {
            var f1 = new Friendship(this, user);
            var f2 = new Friendship(user, this);

            FriendsOf.Remove(f1);
            user.Friends.Remove(f1);

            Friends.Remove(f2);
            user.FriendsOf.Remove(f2);
        }
        
        protected bool Equals(User other)
        {
            return Id == other.Id;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((User) obj);
        }

        public override int GetHashCode()
        {
            return Id;
        }

        public override string ToString()
        {
            return $"{Name} {LastName}";
        }
    }
}