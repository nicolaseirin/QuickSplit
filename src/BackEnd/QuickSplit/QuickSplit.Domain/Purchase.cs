using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace QuickSplit.Domain
{
    public class Purchase
    {
        public Purchase() {}
        
        public Purchase(User purchaser, Group @group, uint cost, Currency currency, IEnumerable<User> participants)
        {
            Purchaser = purchaser;
            Group = @group;
            Cost = cost;
            Currency = currency;
            foreach (User participant in participants)
            {
                AddParticipant(participant);
            }
        }

        public int Id { get; set; }
        
        public User Purchaser { get; set; }
        
        public ICollection<Participant> Participants { get; set; } = new List<Participant>();
        
        public Group Group { get; set; }
        
        public uint Cost { get; set; }
        
        public Currency Currency { get; set; }

        public void AddParticipant(User user)
        {
            var participant = new Participant()
            {
                User = user,
                UserId = user.Id,
                Group = Group,
                GroupId = Group.Id
            };
            
            Participants.Add(participant);
        }

        public void RemoveParticipant(User user)
        {
            var participant = new Participant()
            {
                User = user,
                UserId = user.Id,
                Group = Group,
                GroupId = Group.Id
            };
            
            Participants.Remove(participant);
        }
    }
}