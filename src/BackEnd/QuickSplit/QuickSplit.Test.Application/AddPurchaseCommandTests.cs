using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Purchases.Commands;
using QuickSplit.Domain;
using QuickSplit.Persistence;
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
                Admin = _jon,
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
            Groups.Add(_theNorth);
        }

        [Fact]
        public async void AddPurchaseTest()
        {
            var command = new CreatePurchaseCommand()
            {
                Group = 1,
                Currency = "Usd",
                Participants = new []{ 1, 2},
                Purchaser = 1,
                Cost = 15
            };
            var handler = new CreateAddPurchaseCommandHandler(Context);

            PurchaseModel result = await handler.Handle(command, CancellationToken.None);
            Group @group = await Groups.FindAsync(1);
            User jon = await Users.FindAsync(1);
            User danny = await Users.FindAsync(2);
            User ghost = await Users.FindAsync(3);
            
            Assert.Equal(command.Currency, result.Currency);
            Assert.Equal(command.Group, result.Group);
            Assert.Equal(command.Participants.ToList(), result.Participants.Select(p => p.Id));
            Assert.Equal(command.Cost, result.Cost);
            Assert.Equal(command.Purchaser, result.Purchaser);

            Assert.Single(group.Purchases);
            Purchase purchase = group.Purchases.Single();
            Assert.Equal(jon, purchase.Purchaser);
            Assert.Collection(purchase.Participants, p => p.User.Equals(danny), p => p.User.Equals(ghost) );
        }
        
        [Fact]
        public async void AddPurchaseWithNonExistantGroupTest()
        {
            var command = new CreatePurchaseCommand()
            {
                Group = 911,
                Currency = "Usd",
                Participants = new []{ 1, 2},
                Purchaser = 1,
                Cost = 15
            };
            var handler = new CreateAddPurchaseCommandHandler(Context);
            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
        
        [Fact]
        public async void AddPurchaseWithNonExistantPurchaserTest()
        {
            var command = new CreatePurchaseCommand()
            {
                Group = 1,
                Currency = "Usd",
                Participants = new []{ 1, 2},
                Purchaser = 911,
                Cost = 15
            };
            var handler = new CreateAddPurchaseCommandHandler(Context);
            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
        
        [Fact]
        public async void AddPurchaseWithNonExistantParticipantTest()
        {
            var command = new CreatePurchaseCommand()
            {
                Group = 1,
                Currency = "Usd",
                Participants = new []{ 1, 2, 911},
                Purchaser = 1,
                Cost = 15
            };
            var handler = new CreateAddPurchaseCommandHandler(Context);
            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
        
        [Fact]
        public async void AddPurchaseWithNonExistentCurrencyTest()
        {
            var command = new CreatePurchaseCommand()
            {
                Group = 1,
                Currency = "Peso venezolano",
                Participants = new []{ 1, 2},
                Purchaser = 1,
                Cost = 15
            };
            var handler = new CreateAddPurchaseCommandHandler(Context);
            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
    }
}