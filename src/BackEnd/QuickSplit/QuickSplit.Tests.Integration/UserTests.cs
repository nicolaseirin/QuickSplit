using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.ComTypes;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Configuration.Memory;
using Newtonsoft.Json;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Models;
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


        [Fact, Priority(0)]
        public async void GetNoUsers()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();
            
            Assert.Empty(users);
        }
        
        [Fact, Priority(1)]
        public async void AddFirstUser()
        {

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, _johnSnow);

            response.EnsureSuccessStatusCode();
            
            UserModel responseUser =  await response.DeserializeObject<UserModel>();
            Assert.Equal(_johnSnow.Name, responseUser.Name);
            Assert.Equal(_johnSnow.LastName, responseUser.LastName);
            Assert.Equal(_johnSnow.Mail, responseUser.Mail);
        }
        
        [Fact, Priority(2)]
        public async void VerifyFirstUserAdded()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();

            Assert.Single(users);
            Assert.Contains(users, model => model.Mail == _johnSnow.Mail);
        }
        
        [Fact, Priority(3)]
        public async void AddSecondUser()
        {

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, _robbStark);

            response.EnsureSuccessStatusCode();
            
            UserModel responseUser =  await response.DeserializeObject<UserModel>();
            Assert.Equal(_robbStark.Name, responseUser.Name);
            Assert.Equal(_robbStark.LastName, responseUser.LastName);
            Assert.Equal(_robbStark.Mail, responseUser.Mail);
        }
        
        [Fact, Priority(4)]
        public async void VerifySecondUserAdded()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> users =  await response.DeserializeCollection<UserModel>();
            
            Assert.Equal(2, users.Count());
            Assert.Single(users, model => model.Mail == _robbStark.Mail);
            Assert.Single(users, model => model.Mail == _robbStark.Mail);
        }
        
    }
}