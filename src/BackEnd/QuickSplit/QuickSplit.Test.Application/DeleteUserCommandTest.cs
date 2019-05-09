using System;
using System.Threading;
using QuickSplit.Application.Users.Commands.DeleteUser;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class DeleteUserCommandTest : CommandsTestBase
    {
        [Fact]
        public void DeleteNonExistingUserTest()
        {
            Users.Add(new User()
            {
                Id = 1,
                Name = "john",
                Password = "123",
                Mail = "mail@gmail.com"
            });
            Context.SaveChanges();
            var command = new DeleteUserCommand()
            {
                Id = 2
            };
            var handler = new DeleteUserCommandHandler(Context);

            Assert.Throws<AggregateException>(() => handler.Handle(command, CancellationToken.None).Result);
            Assert.Single(Users);
        }
        
        [Fact]
        public async void DeleteExistingUserTest()
        {
            Users.Add(new User()
            {
                Id = 1,
                Name = "john",
                Password = "123",
                Mail = "mail@gmail.com"
            });
            var command = new DeleteUserCommand()
            {
                Id = 1
            };
            var handler = new DeleteUserCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);

            Assert.Empty(Users);
        }
    }
}