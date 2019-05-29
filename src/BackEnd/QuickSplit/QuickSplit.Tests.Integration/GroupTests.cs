using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore.Internal;
using QuickSplit.Application.Groups;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Groups.Queries;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries;
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

            HttpResponseMessage response = await _client.PostObjectAsync(GroupUrl, group);

            response.EnsureSuccessStatusCode();
            GroupModel responseGroup = await response.DeserializeObject<GroupModel>();
            Assert.Equal(group.Name, responseGroup.Name);
            Assert.Equal(group.Admin, responseGroup.Admin);
            Assert.True(group.Memberships.SequenceEqual(responseGroup.Memberships));
        }

        [Fact, Priority(2)]
        public async void CreateGroupWithNonExistantAdmin()
        {
            var group = new CreateGroupCommand()
            {
                Name = "Red Wedding",
                Admin = 911
            };

            HttpResponseMessage response = await _client.PostObjectAsync(GroupUrl, group);

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

            HttpResponseMessage response = await _client.PostObjectAsync(GroupUrl, group);

            Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
        }

        [Fact, Priority(3)]
        public async void GetGroups()
        {
            var query = new GetGroupsQuery();

            HttpResponseMessage response = await _client.GetAsync(GroupUrl);
            response.EnsureSuccessStatusCode();
            IEnumerable<GroupModel> groups = await response.DeserializeCollection<GroupModel>();

            Assert.Single(groups);
            GroupModel group = groups.Single();
            Assert.Equal(1, group.Id);
            Assert.Equal(1, group.Admin);
            Assert.Equal("Red Wedding", group.Name);
            Assert.True(group.Memberships.All(m => m == 2 || m == 3));
        }

        [Fact, Priority(3)]
        public async void GetMembers()
        {
            HttpResponseMessage response = await _client.GetAsync(GroupUrl + "/1/users");
            response.EnsureSuccessStatusCode();
            IEnumerable<UserModel> members = await response.DeserializeCollection<UserModel>();

            Assert.True(members.All(user => user.Id == 1 || user.Id == 2 || user.Id == 3 ));
        }
        
        [Fact, Priority(4)]
        public async void AddPurchase()
        {
            var command = new AddPurchaseCommand()
            {
                Group = 1,
                Currency = "Usd",
                Participants = new[] {1, 2},
                Purchaser = 1,
                Cost = 15
            };

            HttpResponseMessage response = await _client.PostObjectAsync($"{GroupUrl}/1/purchases", command);
            response.EnsureSuccessStatusCode();
            PurchaseModel purchase = await response.DeserializeObject<PurchaseModel>();
            
            Assert.Equal(command.Currency, purchase.Currency);
            Assert.Equal(command.Group, purchase.Group);
            Assert.Equal(command.Participants.ToList(), purchase.Participants);
            Assert.Equal(command.Cost, purchase.Cost);
            Assert.Equal(command.Purchaser, purchase.Purchaser);
        }
        
        [Fact, Priority(5)]
        public async void GetGroupWithPurchase()
        {
            var query = new GetGroupsQuery();

            HttpResponseMessage response = await _client.GetAsync(GroupUrl + "/1");
            response.EnsureSuccessStatusCode();
            GroupModel group = await response.DeserializeObject<GroupModel>();
            
            Assert.True(group.Purchases.Count(p => p == 1) == 1);
        }
        
        [Fact, Priority(5)]
        public async void GetGroupsAgain()
        {
            var query = new GetGroupsQuery();

            HttpResponseMessage response = await _client.GetAsync(GroupUrl);
            response.EnsureSuccessStatusCode();
            IEnumerable<GroupModel> groups = await response.DeserializeCollection<GroupModel>();

            Assert.Single(groups);
            GroupModel group = groups.Single();
            Assert.Equal(1, group.Id);
            Assert.Equal(1, group.Admin);
            Assert.Equal("Red Wedding", group.Name);
            Assert.True(group.Memberships.All(m => m == 2 || m == 3));
            Assert.True(group.Purchases.Any());
        }
        
        [Fact, Priority(5)]
        public async void GetGroupPurchase()
        {
            var query = new GetGroupsQuery();

            HttpResponseMessage response = await _client.GetAsync(GroupUrl + "/1/purchases");
            response.EnsureSuccessStatusCode();
            PurchaseModel purchase = (await response.DeserializeCollection<PurchaseModel>()).Single();
            
            Assert.Equal(1, purchase.Id);
            Assert.True(purchase.Participants.All(p => p == 2 || p == 1));
            Assert.Equal(1, purchase.Purchaser);
            Assert.Equal(15U, purchase.Cost);
        }
        
        [Fact, Priority(5)]
        public async void GetUserPurchase()
        {
            var query = new GetGroupsQuery();

            HttpResponseMessage response = await _client.GetAsync(UsersUrl + "/1/purchases");
            response.EnsureSuccessStatusCode();
            PurchaseModel purchase = (await response.DeserializeCollection<PurchaseModel>()).Single();
            
            Assert.Equal(1, purchase.Id);
            Assert.True(purchase.Participants.All(p => p == 2 || p == 1));
            Assert.Equal(1, purchase.Purchaser);
            Assert.Equal(15U, purchase.Cost);
        }
    }
}