using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Commands.UpdateUser;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries.GetPassword;
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
        }

        [Fact, Priority(1)]
        public async void GetAdminOnly()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();
            
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
            
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();
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
            
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();
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

        [Fact, Priority(5)]
        public async void DeleteJohnSnow()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/1");
            
            response.EnsureSuccessStatusCode();
        }
        
        [Fact, Priority(6)]
        public async void GetJohnSnow()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/2");

            response.EnsureSuccessStatusCode();

            UserModel responseUser =  await response.DeserializeObject<UserModel>();
            Assert.Equal(_johnSnow.Name, responseUser.Name);
            Assert.Equal(_johnSnow.LastName, responseUser.LastName);
            Assert.Equal(_johnSnow.Mail, responseUser.Mail);
        }
        
        [Fact, Priority(6)]
        public async void CreateUserWithoutPassword()
        {
            var user = new CreateUserCommand()
            {
              Name  = "Hodor",
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
                Name  = "Hodor",
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
                Name  = "Hodor",
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
                Name  = "Hodor",
                Password = "123",
                Mail = "hodor@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);
            
            Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        }
        
        [Fact, Priority(6)]
        public async void CreateUserWithoutExistingMail()
        {
            var user = new CreateUserCommand()
            {
                Name  = "Hodor",
                LastName = "Hodor",
                Password = "123",
                Mail = "admin@gmail.com"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);
            
            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }

        [Fact, Priority(6)]
        public async void GetNonExistantUser()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/911");

            Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        }
        [Fact, Priority(5)]
        public async void DeleteNonExistantUser()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/911");
            
            Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        }
        
        [Fact, Priority(6)]
        public async void DeleteAgainJohnSnow()
        {
            HttpResponseMessage response = await _client.DeleteAsync(UsersUrl + "/1");
            
            response.EnsureSuccessStatusCode();
        }
        
        [Fact, Priority(6)]
        public async void VerifyJohnSnowDoesntExist()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users = await response.DeserializeCollection<UserModel>();

            Assert.DoesNotContain(users, user => user.Name == _johnSnow.Name && user.LastName == _johnSnow.LastName);
        }        
    }
}
