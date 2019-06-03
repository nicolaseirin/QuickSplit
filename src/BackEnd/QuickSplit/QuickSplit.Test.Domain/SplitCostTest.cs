using System.Collections.Generic;
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
            _group.Purchases.Add(new Purchase()
            {
                
            });
        }
    }
}