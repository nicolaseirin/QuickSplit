using System;
using System.Collections.Generic;
using System.Threading;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class CreateGroupCommandTest: CommandsTestBase
    {
        [Fact]
        public async void CreateValidGroupTest()
        {
            var user = new User()
            {
                Name = "Juan",
                LastName = "Cunha",
                Id = 1,
                Mail = "jaun@gmail.com",
                Password = "123asd"
            };
            var user2 = new User()
            {
                Name = "Pedro",
                LastName = "Cunha",
                Id = 2,
                Mail = "pedro@gmail.com",
                Password = "123asd"
            };
            Context.Users.Add(user);
            Context.Users.Add(user2);
            var command = new CreateGroupCommand()
            {
                Name = "La pedrera",
                Admin = 1,
                Memberships = new List<int>()
            };
            command.Memberships.Add(1);
            command.Memberships.Add(2);
            var handler = new CreateGroupCommandHandler(Context);

            GroupModel group = await handler.Handle(command, CancellationToken.None);

            Assert.Contains(Groups, g => g.Id == group.Id);
            Assert.Contains(Memberships, m => m.UserId == user.Id && m.GroupId == group.Id);
            Assert.Contains(Memberships, m => m.UserId == user2.Id && m.GroupId == group.Id);

        }

        [Fact]
        public void CreateGroupWithoutNameTest()
        {
            var command = new CreateGroupCommand()
            {
                Admin = 1,
            };
            var handler = new CreateGroupCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }

        [Fact]
        public void CreateGroupWithoutMembersTest()
        {
            var command = new CreateGroupCommand()
            {
                Admin = 1,
            };
            var handler = new CreateGroupCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }

    }
}

