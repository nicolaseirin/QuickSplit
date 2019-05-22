using System;
using System.Linq;
using System.Threading;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class UpdateUserCommandTest : CommandsTestBase
    {
        public UpdateUserCommandTest()
        {
            var original = new User()
            {
                Id = 1,
                Name = "John",
                LastName = "Doe",
                Password = "Password123",
                Mail = "jonny@gmail.com"
            };
            Users.Add(original);
        }

        [Fact]
        public async void UpdateValidUserTest()
        {

            var command = new UpdateUserCommand()
            {
                Id = 1,
                Name = "Johnnny",
                LastName = "Doeere",
                Password = "Password12er3",
                Mail = "jonny123@gmail.com"
            };
            var handler = new UpdateUserCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);
            User user = Users.Single(u => u.Id == 1);

            Assert.Equal(command.Name, user.Name);
            Assert.Equal(command.Mail, user.Mail);
            Assert.Equal(command.LastName, user.LastName);
        }
        
        [Fact]
        public async void UpdateInvalidUserTest()
        {
            var command = new UpdateUserCommand()
            {
                Name = "Johnnny",
                LastName = "Doeere",
                Password = "Password12er3",
                Mail = "jonny123gmailcom"
            };
            var handler = new UpdateUserCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
        
        [Fact]
        public async void UpdateExistingMailTest()
        {
            Users.Add(new User()
            {
                Id = 2,
                Name = "NotJohn",
                LastName = "NotDoe",
                Password = "NotPassword123",
                Mail = "Notjonny@gmail.com"
            });
            var command = new UpdateUserCommand()
            {
                Id = 2,
                Name = "Johnnny",
                LastName = "Doeere",
                Password = "Password12er3",
                Mail = "jonny123gmailcom"
            };
            var handler = new UpdateUserCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }
        
        [Fact]
        public async void UpdateOnlyNameTest()
        {

            var command = new UpdateUserCommand()
            {
                Id = 1,
                Name = "name"
            };
            var handler = new UpdateUserCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);
            User user = Users.Single(u => u.Id == 1);

            Assert.Equal(command.Name, user.Name);
            Assert.NotNull(user.LastName);
            Assert.NotNull(user.Mail);
        }
        
        [Fact]
        public async void UpdateOnlyLastNameTest()
        {

            var command = new UpdateUserCommand()
            {
                Id = 1,
                LastName = "name"
            };
            var handler = new UpdateUserCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);
            User user = Users.Single(u => u.Id == 1);

            Assert.Equal(command.LastName, user.LastName);
            Assert.NotNull(user.Name);
            Assert.NotNull(user.Mail);
        }
        
        [Fact]
        public async void UpdateOnlyMailTest()
        {

            var command = new UpdateUserCommand()
            {
                Id = 1,
                Mail = "otherMail@gmail.com"
            };
            var handler = new UpdateUserCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);
            User user = Users.Single(u => u.Id == 1);

            Assert.Equal(command.Mail, user.Mail);
            Assert.NotNull(user.Name);
            Assert.NotNull(user.LastName);
        }
    }
}