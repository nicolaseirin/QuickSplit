using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Microsoft.EntityFrameworkCore.Internal;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Models
{
    public class PurchaseModel
    {
        
        public PurchaseModel() {}
        public PurchaseModel(Purchase purchase)
        {

            Id = purchase.Id;
            Name = purchase.Name;
            Cost = purchase.Cost;
            Currency = purchase.Currency.ToString();
            Group = purchase.Group.Id;
            Participants = purchase.Participants.Select(participant => new UserModel(participant.User));
            Purchaser = purchase.Purchaser.Id;
            Longitude = purchase.Longitude;
            Latitude = purchase.Latitude;
            
        }
        
        public int Id { get; set; }
        
        public string Name { get; set; }
        
        public int Purchaser { get; set; }

        public int Group { get; set; }

        public IEnumerable<UserModel> Participants { get; set; } = new List<UserModel>();

        public double Cost { get; set; }

        public string Currency { get; set; }

        public double Longitude { get; set; }

        public double Latitude { get; set; }
    }
}