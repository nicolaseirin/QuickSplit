using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries;
using QuickSplit.Tests.Integration.Internal;
using QuickSplit.WebApi;
using Xunit;
using Xunit.Priority;

namespace QuickSplit.Tests.Integration
{
    [TestCaseOrderer(PriorityOrderer.Name, PriorityOrderer.Assembly)]
    public class UserTests : IClassFixture<CustomWebApplicationFactory>
    {
        private const string UsersUrl = "/api/users";
        private readonly HttpClient _client;

        private CreateUserCommand _johnSnow;
        private CreateUserCommand _robbStark;
        private CreateUserCommand _ghost;
        private CreateUserCommand _sansa;
        private CreateUserCommand _bran;

        public UserTests(CustomWebApplicationFactory factory)
        {
            _client = factory.CreateClient();

            InitializeUsers();
            SetAdminToken();
        }

        private void SetAdminToken()
        {
            var admin = new PasswordIsValidQuery()
            {
                Mail = "admin@gmail.com",
                Password = "123"
            };

            HttpResponseMessage response = _client.PostObjectAsync("/api/authentications", admin).Result;

            TokenModel token = response.DeserializeObject<TokenModel>().Result;
            _client.DefaultRequestHeaders.Add("Authorization", "Bearer " + token.Token);
        }

        private void InitializeUsers()
        {
            _johnSnow = new CreateUserCommand()
            {
                Name = "John",
                LastName = "Snow",
                Mail = "snow@gmail.com",
                Password = "123"
            };

            _robbStark = new CreateUserCommand()
            {
                Name = "Robb",
                LastName = "Stark",
                Mail = "Robb@gmail.com",
                Password = "123"
            };
            _sansa = new CreateUserCommand()
            {
                Name = "Sansa",
                LastName = "Stark",
                Mail = "San@gmail.com",
                Password = "123"
            };
            _bran = new CreateUserCommand()
            {
                Name = "Bran",
                LastName = "Stark",
                Mail = "bran@gmail.com",
                Password = "123"
            };
            _robbStark = new CreateUserCommand()
            {
                Name = "Robb",
                LastName = "Stark",
                Mail = "Robb@gmail.com",
                Password = "123"
            };
            _ghost = new CreateUserCommand()
            {
                Name = "Ghost",
                Mail = "ghost@gmail.com",
                Password = "123"
            };
        }

        [Fact, Priority(1)]
        public async void GetAdminOnly()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();

            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.Single(users);
        }

        [Fact, Priority(2)]
        public async void AddFirstUser()
        {
            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, _johnSnow);

            response.EnsureSuccessStatusCode();

            UserModel responseUser = await response.DeserializeObject<UserModel>();
            Assert.Equal(_johnSnow.Name, responseUser.Name);
            Assert.Equal(_johnSnow.LastName, responseUser.LastName);
            Assert.Equal(_johnSnow.Mail, responseUser.Mail);
        }

        [Fact, Priority(3)]
        public async void VerifyFirstUserAdded()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();

            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();
            Assert.Equal(2, users.Count());
            Assert.Contains(users, model => model.Mail == _johnSnow.Mail);
        }

        [Fact, Priority(4)]
        public async void AddSecondUser()
        {
            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, _robbStark);

            response.EnsureSuccessStatusCode();

            UserModel responseUser = await response.DeserializeObject<UserModel>();
            Assert.Equal(_robbStark.Name, responseUser.Name);
            Assert.Equal(_robbStark.LastName, responseUser.LastName);
            Assert.Equal(_robbStark.Mail, responseUser.Mail);
        }

        [Fact, Priority(5)]
        public async void VerifySecondUserAdded()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();

            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();
            Assert.Equal(3, users.Count());
            Assert.Single(users, model => model.Mail == _robbStark.Mail);
            Assert.Single(users, model => model.Mail == _robbStark.Mail);
        }

        [Fact, Priority(4)]
        public async void ChangeAdminNameAndLastName()
        {
            var update = new UpdateUserCommand()
            {
                Name = "NotAdmin",
                LastName = "NotAdmin"
            };

            HttpResponseMessage response = await _client.PutObjectAsync(UsersUrl + "/1", update);

            response.EnsureSuccessStatusCode();
            UserModel user = await response.DeserializeObject<UserModel>();

            Assert.Equal(update.Name, user.Name);
            Assert.Equal(update.LastName, user.LastName);
        }

        [Fact, Priority(4)]
        public async void ChangeJohnSnowMail()
        {
            var update = new UpdateUserCommand()
            {
                Mail = "jonny@gmail.com"
            };

            HttpResponseMessage response = await _client.PutObjectAsync(UsersUrl + "/2", update);

            response.EnsureSuccessStatusCode();
            UserModel user = await response.DeserializeObject<UserModel>();

            Assert.Equal(update.Mail, user.Mail);
        }


        [Fact, Priority(4)]
        public async void ChangeJohnSnowMailBackToOriginal()
        {
            var update = new UpdateUserCommand()
            {
                Mail = "snow@gmail.com"
            };

            HttpResponseMessage response = await _client.PutObjectAsync(UsersUrl + "/2", update);

            response.EnsureSuccessStatusCode();
            UserModel user = await response.DeserializeObject<UserModel>();

            Assert.Equal(update.Mail, user.Mail);
        }


        [Fact, Priority(6)]
        public async void GetJohnSnow()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/2");

            response.EnsureSuccessStatusCode();

            UserModel responseUser = await response.DeserializeObject<UserModel>();
            Assert.Equal(_johnSnow.Name, responseUser.Name);
            Assert.Equal(_johnSnow.LastName, responseUser.LastName);
            Assert.Equal(_johnSnow.Mail, responseUser.Mail);
        }

        [Fact, Priority(6)]
        public async void CreateUserWithoutPassword()
        {
            var user = new CreateUserCommand()
            {
                Name = "Hodor",
                LastName = "Hodor",
                Mail = "hodor@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void CreateUserWithoutMail()
        {
            var user = new CreateUserCommand()
            {
                Name = "Hodor",
                LastName = "Hodor",
                Password = "123"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void CreateUserWithoutName()
        {
            var user = new CreateUserCommand()
            {
                LastName = "Hodor",
                Password = "123",
                Mail = "hodor@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void CreateUserWithoutLastName()
        {
            var user = new CreateUserCommand()
            {
                Name = "Hodor",
                Password = "123",
                Mail = "hodor@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void CreateUserWithExistingMail()
        {
            var user = new CreateUserCommand()
            {
                Name = "Hodor",
                LastName = "Hodor",
                Password = "123",
                Mail = "admin@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }

        [Fact, Priority(7)]
        public async void GetNonExistantUser()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/911");

            Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void AddGhost()
        {
            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, _ghost);

            response.EnsureSuccessStatusCode();

            UserModel responseUser = await response.DeserializeObject<UserModel>();
            Assert.Equal(_ghost.Name, responseUser.Name);
            Assert.Equal(_ghost.LastName, responseUser.LastName);
            Assert.Equal(_ghost.Mail, responseUser.Mail);
        }

        [Fact, Priority(7)]
        public async void AddJohnAndGhostAsFriends()
        {
            var body = new AddFriendCommand()
            {
                FriendUserId = 3
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl + "/1/friends", body);

            response.EnsureSuccessStatusCode();
        }

        [Fact, Priority(7)]
        public async void AddJohnAndRobbAsFriends()
        {
            var body = new AddFriendCommand()
            {
                FriendUserId = 2
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl + "/1/friends", body);

            response.EnsureSuccessStatusCode();
        }

        [Fact, Priority(8)]
        public async void VerifyJohnFriendships()
        {
            var body = new GetFriendsQuery()
            {
                UserId = 1
            };

            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/1/friends");

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> friends = await response.DeserializeCollection<UserModel>();
            Assert.True(friends.All(user => user.Id == 2 || user.Id == 3));
        }

        [Fact, Priority(8)]
        public async void VerifyGhostFriendships()
        {
            var body = new GetFriendsQuery()
            {
                UserId = 1
            };

            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/3/friends");

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> friends = await response.DeserializeCollection<UserModel>();
            Assert.True(friends.All(user => user.Id == 1));
        }

        [Fact, Priority(7)]
        public async void DeleteNonExistantUser()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/911");

            Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        }

        [Fact, Priority(9)]
        public async void GetNonFriendsOfHodor()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?excludeFriendsOfId=5");
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.Contains(users, user => user.Id == 1);
            Assert.Contains(users, user => user.Id == 2);
            Assert.Contains(users, user => user.Id == 3);
            Assert.Contains(users, user => user.Id == 4);
            Assert.DoesNotContain(users, user => user.Id == 5);
        }

        [Fact, Priority(9)]
        public async void GetNonFriendsOfJohn()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?excludeFriendsOfId=2");
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.DoesNotContain(users, user => user.Id == 1);
            Assert.DoesNotContain(users, user => user.Id == 2);
            Assert.Contains(users, user => user.Id == 3);
            Assert.Contains(users, user => user.Id == 4);
            Assert.Contains(users, user => user.Id == 5);
        }

        [Fact, Priority(9)]
        public async void GetNonFriendsOfAdmin()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?excludeFriendsOfId=1");
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.DoesNotContain(users, user => user.Id == 1);
            Assert.DoesNotContain(users, user => user.Id == 2);
            Assert.DoesNotContain(users, user => user.Id == 3);
            Assert.Contains(users, user => user.Id == 4);
            Assert.Contains(users, user => user.Id == 5);
        }

        [Fact, Priority(9)]
        public async void AddStarks()
        {
            HttpResponseMessage response1 = await _client.PostObjectAsync(UsersUrl, _sansa);
            HttpResponseMessage response2 = await _client.PostObjectAsync(UsersUrl, _bran);


            response1.EnsureSuccessStatusCode();
            response2.EnsureSuccessStatusCode();
        }

        [Fact, Priority(10)]
        public async void FindStarks()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?find=Stark");

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.True(users.All(user => user.LastName == "Stark"));
        }

        [Fact, Priority(10)]
        public async void FindHodor()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?find=Hod");

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.True(users.Count() == 1);
            Assert.Contains(users, user => user.Name == "Hodor");
        }

        [Fact, Priority(10)]
        public async void FindNonExistant()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?find=Targe");

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.True(!users.Any());
        }

        [Fact, Priority(9)]
        public async void GetNonFriendsOfJohnThatAreStark()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "?excludeFriendsOfId=3&find=Stark");
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.True(users.All(user => user.Id == 6 || user.Id == 7));
        }

        [Fact, Priority(10)]
        public async void DeleteJohnGhostFriendship()
        {
            HttpResponseMessage response = await _client.DeleteAsync($"{UsersUrl}/2/friends/3");

            response.EnsureSuccessStatusCode();
        }

        [Fact, Priority(11)]
        public async void VerifyJohnGhostFriendshipWasDeleted()
        {
            HttpResponseMessage response1 = await _client.GetAsync($"{UsersUrl}/2/friends");
            HttpResponseMessage response2 = await _client.GetAsync($"{UsersUrl}/3/friends");
            response1.EnsureSuccessStatusCode();
            response2.EnsureSuccessStatusCode();
            IEnumerable<UserModel> johnFriends = await response1.DeserializeCollection<UserModel>();
            IEnumerable<UserModel> ghostFriends = await response2.DeserializeCollection<UserModel>();

            Assert.DoesNotContain(johnFriends, u => u.Id == 3);
            Assert.DoesNotContain(ghostFriends, u => u.Id == 2);
        }

        [Fact, Priority(100)]
        public async void DeleteJohnSnow()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/2");

            response.EnsureSuccessStatusCode();
        }

        [Fact, Priority(100)]
        public async void DeleteJohnSnowAgain()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/2");

            response.EnsureSuccessStatusCode();
        }

        [Fact, Priority(101)]
        public async void VerifyJohnSnowDoesntExist()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.DoesNotContain(users, user => user.Name == _johnSnow.Name && user.LastName == _johnSnow.LastName);
        }
    }
}