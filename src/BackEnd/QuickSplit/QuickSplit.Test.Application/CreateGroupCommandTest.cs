using System;
using System.Collections.Generic;
using System.Threading;
using QuickSplit.Application.Groups.Commands.CreateGroup;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Users.Commands.CreateUser;
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
            Context.Users.Add(user);
            var command = new CreateGroupCommand()
            {
                Name = "La pedrera",
                Admin = 1,
                Memberships = new List<int>()
            };
            var handler = new CreateGroupCommandHandler(Context);

            GroupModel group = await handler.Handle(command, CancellationToken.None);

            Assert.Contains(Groups, g => g.Id == group.Id);
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

    }
}

