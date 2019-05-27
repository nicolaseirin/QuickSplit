using System.Collections.Generic;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class AddPurchaseCommandTests : CommandsTestBase
    {
        private readonly User _jon;
        private readonly User _danny;
        private readonly User _ghost;
        private readonly Group _theNorth;

        public AddPurchaseCommandTests()
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
            _theNorth = new Group()
            {
                Id = 1,
                Admin = 1,
                Name = "The North",
                Memberships = new List<Membership>()
                {
                    new Membership() {Group = _theNorth, GroupId = 1, User = _danny, UserId = 2},
                    new Membership() {Group = _theNorth, GroupId = 1, User = _ghost, UserId = 3}
                }
            };
            Users.Add(_jon);
            Users.Add(_danny);
            Users.Add(_ghost);
        }

        [Fact]
        public void AddPurchaseTest()
        {
            var command = new AddPurchaseCommand()
            {
                Group = 1,
                Currency = "Usd",
                Participants = new []{ 1, 2},
                Purchaser = 1,
                Cost = 15
            };
            var handler = new AddPurchaseCommandHandler(Context);
            
            
        }
    }
}