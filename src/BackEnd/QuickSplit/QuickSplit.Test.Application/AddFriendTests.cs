using System.Threading;
using QuickSplit.Application.Users.Commands.AddFriendCommand;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class AddFriendTests : CommandsTestBase
    {
        private readonly User _jon;
        private readonly User _danny;
        private readonly User _ghost;

        public AddFriendTests()
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
            Users.Add(_jon);
            Users.Add(_danny);
            Users.Add(_ghost);
        }

        [Fact]
        public async void AddFriendTest()
        {
            var command = new AddFriendCommand()
            {
                FriendUserId = 1,
                CurrentUserId = 2 
            };
            var handler = new AddFriendCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);

            Assert.Contains(Friendships, friendship => friendship.Friend1Id == 1 && friendship.Friend2Id == 2);
            Assert.DoesNotContain(Friendships, friendship => friendship.Friend1Id == 3 || friendship.Friend2Id == 3);
            Assert.NotEmpty(_jon.Friends);
            Assert.NotEmpty(_jon.FriendsOf);
            Assert.NotEmpty(_danny.Friends);
            Assert.NotEmpty(_danny.FriendsOf);
            Assert.Empty(_ghost.Friends);
            Assert.Empty(_ghost.FriendsOf);
        }
        
        [Fact]
        public async void AddMultipleFriendTest()
        {
            var command1 = new AddFriendCommand()
            {
                FriendUserId = 1,
                CurrentUserId = 2 
            };
            var command2 = new AddFriendCommand()
            {
                FriendUserId = 1,
                CurrentUserId = 3 
            };
            var handler = new AddFriendCommandHandler(Context);

            await handler.Handle(command1, CancellationToken.None);
            await handler.Handle(command2, CancellationToken.None);

            Assert.Contains(Friendships, friendship => friendship.Friend1Id == 1 && friendship.Friend2Id == 2);
            Assert.Contains(Friendships, friendship => friendship.Friend1Id == 1 && friendship.Friend2Id == 3);
            Assert.NotEmpty(_jon.Friends);
            Assert.NotEmpty(_jon.FriendsOf);
            Assert.NotEmpty(_danny.Friends);
            Assert.NotEmpty(_danny.FriendsOf);
            Assert.NotEmpty(_ghost.Friends);
            Assert.NotEmpty(_ghost.FriendsOf);
        }
    }
}