using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Microsoft.EntityFrameworkCore.Internal;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Models
{
    public class PurchaseModel
    {
        
        public PurchaseModel() {}
        public PurchaseModel(Purchase purchase)
        {
            if (purchase.Purchaser == null || purchase.Group == null || purchase.Participants == null || purchase.Participants.Any(participant => participant == null))
                Console.WriteLine("e");
            Id = purchase.Id;
            Name = purchase.Name;
            Cost = purchase.Cost;
            Currency = purchase.Currency.ToString();
            Group = purchase.Group.Id;
            Participants = purchase.Participants.Select(participant => participant.UserId);
            Purchaser = purchase.Purchaser.Id;
        }
        
        public int Id { get; set; }
        
        public string Name { get; set; }
        
        public int Purchaser { get; set; }

        public int Group { get; set; }

        public IEnumerable<int> Participants { get; set; } = new List<int>();

        public double Cost { get; set; }

        public string Currency { get; set; }
    }
}