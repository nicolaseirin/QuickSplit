using System;
using System.Collections.Generic;
using System.Threading;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class LeaveGroupCommandTest: CommandsTestBase
    {
        [Fact]
        public void LeaveNonExistingGroup()
        {
            var command = new LeaveGroupCommand()
            {
                UserId = 1,
                GroupId = 1
            };
            var handler = new LeaveGroupCommandHandler(Context);

            Assert.Throws<AggregateException>(() => handler.Handle(command, CancellationToken.None).Result);
        }

        [Fact]
        public void LeaveNonExistingUser()
        {
            var command = new LeaveGroupCommand()
            {
                UserId = 1,
                GroupId = 1
            };
            var handler = new LeaveGroupCommandHandler(Context);

            Assert.Throws<AggregateException>(() => handler.Handle(command, CancellationToken.None).Result);
        }

        [Fact]
        public async void LeaveExistingGroupOk()
        {
            var user = new User()
            {
                Name = "Sofia",
                LastName = "Cunha",
                Mail = "sofi@gmail.com",
                Password = "sofi123"
            };
            Context.Users.Add(user);
            var group = new Group()
            {
                Name = "La pedrera",
                Admin = user,
                Memberships = new List<Membership>()
            };
            Context.Groups.Add(group);
            var mem = new Membership()
            {
                User = user,
                Group = group,
                UserId = user.Id,
                GroupId = group.Id
            };
            Context.Memberships.Add(mem);
            group.Memberships.Add(mem);
            var command = new LeaveGroupCommand()
            {
                UserId = user.Id,
                GroupId = group.Id
            };
            var handler = new LeaveGroupCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);

            Assert.Empty(Memberships);
            Assert.NotEmpty(Groups);
        }
    }
}



