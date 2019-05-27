using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Tests.Integration.Internal;
using Xunit;

namespace QuickSplit.Tests.Integration
{
    public class LoginTests: IClassFixture<CustomWebApplicationFactory>
    {
        private const string UsersUrl = "/api/users";
        private readonly HttpClient _client;

        public LoginTests(CustomWebApplicationFactory factory)
        {
            _client = factory.CreateClient();
        }

        [Fact]
        public async void GetAllWithoutToken()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl);

            Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
        }
        
        [Fact]
        public async void GetWithoutToken()
        {
            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/1");

            Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
        }

        [Fact]
        public async void PostWithoutToken()
        {
            var user = new CreateUserCommand()
            {
                Name = "John",
                LastName = "Snow",
                Mail = "snow@gmail.com",
                Password = "123"
            };

            HttpResponseMessage response = await _client.PostObjectAsync(UsersUrl, user);

            Assert.Equal(HttpStatusCode.Created, response.StatusCode);
        }
    }
}