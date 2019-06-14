using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace QuickSplit.Domain
{
    public class Purchase
    {
        public Purchase() {}
        

        public Purchase(User purchaser, Group @group, double cost, Currency currency, IEnumerable<User> participants, string name, double longitude, double latitude)
        {
            Purchaser = purchaser;
            Group = @group;
            Cost = cost;
            Currency = currency;
            Name = name;
            Longitude = longitude;
            Latitude = latitude;
            foreach (User participant in participants)
            {
                AddParticipant(participant);
            }
        }

        public int Id { get; set; }
        
        public string Name { get; set; }
        
        public virtual User Purchaser { get; set; }
        
        public virtual ICollection<Participant> Participants { get; set; } = new List<Participant>();
        
        public virtual Group Group { get; set; }
        
        public double Cost { get; set; }
        
        public virtual Currency Currency { get; set; }

        public double Longitude { get; set; }

        public double Latitude { get; set; }

        public void AddParticipant(User user)
        {
            var participant = new Participant()
            {
                User = user,
                UserId = user.Id,
                Purchase = this,
                PurchaseId = Id
            };
            
            Participants.Add(participant);
        }

        public void RemoveParticipant(User user)
        {
            var participant = new Participant()
            {
                User = user,
                UserId = user.Id,
                Purchase = this,
                PurchaseId = Id
            };
            
            Participants.Remove(participant);
        }

        protected bool Equals(Purchase other)
        {
            return Id == other.Id;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Purchase) obj);
        }

        public override int GetHashCode()
        {
            return Id;
        }
    }
}