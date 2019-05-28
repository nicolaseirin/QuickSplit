using System;
using System.Threading;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Domain;
using QuickSplit.Persistence;
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
            var handler = new DeleteUserCommandHandler(Context, new ImageRepository());
            
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
            var handler = new DeleteUserCommandHandler(Context, new ImageRepository());

            await handler.Handle(command, CancellationToken.None);

            Assert.Empty(Users);
        }
    }
}