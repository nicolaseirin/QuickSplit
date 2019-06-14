using System;
using System.Collections.Generic;
using System.Linq;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Domain
{
    public class SplitCostTest 
    {
        private readonly User _jon;
        private readonly User _danny;
        private readonly User _ghost;
        private readonly User _tyrion;
        private readonly Group _group;

        public SplitCostTest()
        {
            _jon = new User()
            {
                Id = 1,
                Name = "Jon",
                LastName = "Snow",
                Mail = "jon@gmail.com",
                Password = "123"
            };
            _danny = new User()
            {
                Id = 2,
                Name = "Daneris",
                LastName = "Targeryan",
                Mail = "danny@gmail.com",
                Password = "123"
            };
            _ghost = new User()
            {
                Id = 3,
                Name = "ghost",
                Mail = "ghost@gmail.com",
                Password = "123"
            };
            _tyrion = new User()
            {
                Id = 4,
                Name = "Tyrion",
                LastName = "Lannister",
                Mail = "tyrion@gmail.com",
                Password = "123"
            };
            _group = new Group()
            {
                Name = "Kings landing party",
                Id = 1,
                Admin = _danny,
            };
            _group.Memberships = new List<Membership>()
            {
                new Membership(_ghost, _group),
                new Membership(_jon, _group),
                new Membership(_tyrion, _group)
            };
        }
        
        
        [Fact]
        public void SinglePurchase()
        {
            var purchase = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase),
                new Participant(_ghost, purchase),
                new Participant(_tyrion, purchase)
            };
            _group.Purchases.Add(purchase);

            SplitCostReport report = _group.GenerateSplitCostReport(Currency.Usd);
            
            Assert.True(report.Dictionary.All(pair => pair.Key.Item2.Equals(_danny) && Math.Abs(pair.Value - 25) < 0.01d));
        }
        
        [Fact]
        public void MultiplePurchasesSamePurchaser()
        {
            var purchase1 = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase1.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase1),
                new Participant(_ghost, purchase1),
                new Participant(_tyrion, purchase1)
            };
            _group.Purchases.Add(purchase1);
            var purchase2 = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase2.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase2),
                new Participant(_ghost, purchase2),
                new Participant(_tyrion, purchase2)
            };
            _group.Purchases.Add(purchase2);

            SplitCostReport report = _group.GenerateSplitCostReport(Currency.Usd);
            
            Assert.True(report.Dictionary.All(pair => pair.Key.Item2.Equals(_danny) && Math.Abs(pair.Value - 50) < 0.01d));
        }
        
        [Fact]
        public void MultiplePurchasesDifferentPurchaser()
        {
            var purchase1 = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase1.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase1),
                new Participant(_ghost, purchase1),
                new Participant(_tyrion, purchase1)
            };
            _group.Purchases.Add(purchase1);
            
            var purchase2 = new Purchase()
            {
                Name = "Birras",
                Id = 2,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase2.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase2),
                new Participant(_ghost, purchase2),
                new Participant(_tyrion, purchase2)
            };
            _group.Purchases.Add(purchase2);
            
            
            var purchase3 = new Purchase()
            {
                Name = "OtrasBirras",
                Id = 3,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _jon
            };
            purchase3.Participants = new List<Participant>()
            {
                new Participant(_danny, purchase3),
                new Participant(_ghost, purchase3),
                new Participant(_tyrion, purchase3)
            };
            _group.Purchases.Add(purchase3);

            SplitCostReport report = _group.GenerateSplitCostReport(Currency.Usd);
            
            Assert.Equal(25d, report[(_jon, _danny)]);
            Assert.Equal(50d, report[(_ghost, _danny)]);
            Assert.Equal(50d, report[(_tyrion, _danny)]);
            Assert.False(report.Dictionary.ContainsKey((_danny, _jon)));
        }
        
        [Fact]
        public void MultiplePurchasesSamePurchaserWithoutAllParticipating()
        {
            var purchase1 = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase1.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase1),
                new Participant(_ghost, purchase1),
                new Participant(_tyrion, purchase1)
            };
            _group.Purchases.Add(purchase1);
            var purchase2 = new Purchase()
            {
                Name = "Birras",
                Id = 1,
                Cost = 100,
                Currency = Currency.Usd,
                Group = _group,
                Purchaser = _danny
            };
            purchase2.Participants = new List<Participant>()
            {
                new Participant(_jon, purchase2)
            };
            _group.Purchases.Add(purchase2);

            SplitCostReport report = _group.GenerateSplitCostReport(Currency.Uyu);
            
            Assert.Equal(75d * 30, report[(_jon, _danny)]);
            Assert.Equal(25d * 30, report[(_ghost, _danny)]);
            Assert.Equal(25d * 30, report[(_tyrion, _danny)]);
            Assert.DoesNotContain(report.Dictionary, pair => pair.Key.Item1.Equals(_danny));
        }
    }
}