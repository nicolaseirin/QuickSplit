using System.Collections.Generic;
using System.Text.RegularExpressions;

namespace QuickSplit.Domain
{
    public class Purchase
    {
        public int Id { get; set; }
        
        public User Purchased { get; set; }
        
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