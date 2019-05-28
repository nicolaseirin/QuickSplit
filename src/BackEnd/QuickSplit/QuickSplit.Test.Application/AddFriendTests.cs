using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries;
using QuickSplit.Domain;
using QuickSplit.Persistence;
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
        public async void CheckNoFriendshipsTest()
        {
            var handler = new GetFriendsQueryHandler(Context, new ImageRepository());

            IEnumerable<UserModel> friends1 = await handler.Handle(new GetFriendsQuery() {UserId = 1}, CancellationToken.None);
            IEnumerable<UserModel> friends2 = await handler.Handle(new GetFriendsQuery() {UserId = 2}, CancellationToken.None);
            IEnumerable<UserModel> friends3 = await handler.Handle(new GetFriendsQuery() {UserId = 3}, CancellationToken.None);

            Assert.Empty(friends1);
            Assert.Empty(friends2);
            Assert.Empty(friends3);
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
            var getHandler = new GetFriendsQueryHandler(Context, new ImageRepository());

            await handler.Handle(command, CancellationToken.None);
            IEnumerable<UserModel> friends1 = await getHandler.Handle(new GetFriendsQuery() {UserId = 1}, CancellationToken.None);
            IEnumerable<UserModel> friends2 = await getHandler.Handle(new GetFriendsQuery() {UserId = 2}, CancellationToken.None);
            IEnumerable<UserModel> friends3 = await getHandler.Handle(new GetFriendsQuery() {UserId = 3}, CancellationToken.None);

            Assert.Single(friends1, user => user.Id == 2);
            Assert.Single(friends2, user => user.Id == 1);
            Assert.Empty(friends3);
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
            var getHandler = new GetFriendsQueryHandler(Context, new ImageRepository());

            await handler.Handle(command1, CancellationToken.None);
            await handler.Handle(command2, CancellationToken.None);
            IEnumerable<UserModel> friends1 = await getHandler.Handle(new GetFriendsQuery() {UserId = 1}, CancellationToken.None);
            IEnumerable<UserModel> friends2 = await getHandler.Handle(new GetFriendsQuery() {UserId = 2}, CancellationToken.None);
            IEnumerable<UserModel> friends3 = await getHandler.Handle(new GetFriendsQuery() {UserId = 3}, CancellationToken.None);

            Assert.True(friends1.All(user => user.Id == 2 || user.Id == 3));
            Assert.Single(friends2, user => user.Id == 1);
            Assert.Single(friends3, user => user.Id == 1);
            Assert.Contains(Friendships, friendship => friendship.Friend1Id == 1 && friendship.Friend2Id == 2);
            Assert.Contains(Friendships, friendship => friendship.Friend1Id == 1 && friendship.Friend2Id == 3);
            Assert.NotEmpty(_jon.Friends);
            Assert.NotEmpty(_jon.FriendsOf);
            Assert.NotEmpty(_danny.Friends);
            Assert.NotEmpty(_danny.FriendsOf);
            Assert.NotEmpty(_ghost.Friends);
            Assert.NotEmpty(_ghost.FriendsOf);
        }

        [Fact]
        public async void AddNonExistantFriend()
        {
            var command = new AddFriendCommand()
            {
                FriendUserId = 911,
                CurrentUserId = 912
            };
            var handler = new AddFriendCommandHandler(Context);

            Assert.ThrowsAny<Exception>(() => handler.Handle(command, CancellationToken.None).Result);
        }

    }
}