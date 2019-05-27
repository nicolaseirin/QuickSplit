using System.Collections.Generic;
using System.Linq;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Commands
{
    public class PurchaseModel
    {
        public PurchaseModel(Purchase purchase)
        {
            Id = purchase.Id;
            Cost = purchase.Cost;
            Currency = purchase.Currency.ToString();
            Group = purchase.Group.Id;
            Participants = purchase.Participants.Select(participant => participant.UserId);
            Purchaser = purchase.Purchaser.Id;
        }
        
        public int Id { get; set; }
        
        public int Purchaser { get; set; }

        public int Group { get; set; }

        public IEnumerable<int> Participants { get; set; }

        public uint Cost { get; set; }

        public string Currency { get; set; }
    }
}