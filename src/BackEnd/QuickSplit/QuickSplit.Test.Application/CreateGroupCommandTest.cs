using System;
using System.Threading;
using QuickSplit.Application.Groups.Commands.CreateGroup;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Models;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class CreateGroupCommandTest: CommandsTestBase
    {
        [Fact]
        public async void CreateValidGroupTest()
        {
            var command = new CreateGroupCommand()
            {
                Name = "La pedrera",
                Admin = "John"
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
                Admin = "Doe",
            };
            var handler = new CreateGroupCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }

        [Fact]
        public async void CreategroupWithoutAdmin()
        {
            var command = new CreateGroupCommand()
            {
                Name = "La pedrera",
                Admin = null,
            };
            var handler = new CreateGroupCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
    }
}

