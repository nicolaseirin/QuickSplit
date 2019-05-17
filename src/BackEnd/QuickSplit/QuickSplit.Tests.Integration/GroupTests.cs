using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using QuickSplit.Application.Groups.Commands.CreateGroup;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Queries.GetPassword;
using QuickSplit.Tests.Integration.Internal;
using Xunit;
using Xunit.Priority;

namespace QuickSplit.Tests.Integration
{
    [TestCaseOrderer(PriorityOrderer.Name, PriorityOrderer.Assembly)]
    public class GroupTests : IClassFixture<CustomWebApplicationFactory>
    {
        private const string UsersUrl = "/api/users";
        private const string GroupUrl = "/api/groups";
        private readonly HttpClient _client;

        private CreateUserCommand _johnSnow;
        private CreateUserCommand _robbStark;
        private CreateUserCommand _tywinLannister;

        public GroupTests(CustomWebApplicationFactory factory)
        {
            _client = factory.CreateClient();

            InitializeAndAddUsers();
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

        private void InitializeAndAddUsers()
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
            _tywinLannister = new CreateUserCommand()
            {
                Name = "Tywin",
                LastName = "Lannister",
                Mail = "lan@gmail.com",
                Password = "123"
            };
        }
        
        [Fact, Priority(1)]
        public async void AddUsers()
        {
            var t1 = _client.PostObjectAsync(UsersUrl, _johnSnow);
            var t2 = _client.PostObjectAsync(UsersUrl, _robbStark);
            var t3 = _client.PostObjectAsync(UsersUrl, _tywinLannister);
            Task.WaitAll(t1, t2, t3);

            t1.Result.EnsureSuccessStatusCode();
            t2.Result.EnsureSuccessStatusCode();
            t3.Result.EnsureSuccessStatusCode();
        }
        
        [Fact, Priority(2)]
        public async void CreateGroupWithAllUsers()
        {
            var group = new CreateGroupCommand()
            {
                Name = "Red Wedding",
                Admin = 1,
                Memberships = {2, 3}
            };

            HttpResponseMessage response = await  _client.PostObjectAsync(GroupUrl, group);

            response.EnsureSuccessStatusCode();
            GroupModel responseGroup = await response.DeserializeObject<GroupModel>();
            Assert.Equal(group.Name, responseGroup.Name);
            Assert.Equal(group.Admin, responseGroup.Admin);
            Assert.True(group.Memberships.SequenceEqual(responseGroup.Memberships));
        }
        
        [Fact, Priority(2)]
        public async void CreateEmptyGroup()
        {
            var group = new CreateGroupCommand()
            {
                Name = "Red Wedding",
                Admin = 1
            };

            HttpResponseMessage response = await  _client.PostObjectAsync(GroupUrl, group);

            response.EnsureSuccessStatusCode();
            GroupModel responseGroup = await response.DeserializeObject<GroupModel>();
            Assert.Equal(group.Name, responseGroup.Name);
            Assert.Equal(group.Admin, responseGroup.Admin);
            Assert.False(group.Memberships.Any());
        }
        
        [Fact, Priority(2)]
        public async void CreateGroupWithNonExistantAdmin()
        {
            var group = new CreateGroupCommand()
            {
                Name = "Red Wedding",
                Admin = 911
            };

            HttpResponseMessage response = await  _client.PostObjectAsync(GroupUrl, group);
            
            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }
        
        [Fact, Priority(2)]
        public async void CreateGroupWithNonExistantMember()
        {
            var group = new CreateGroupCommand()
            {
                Name = "Red Wedding",
                Admin = 1,
                Memberships = {2, 3, 911}
            };

            HttpResponseMessage response = await  _client.PostObjectAsync(GroupUrl, group);
            
            Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        }
        
    }
}