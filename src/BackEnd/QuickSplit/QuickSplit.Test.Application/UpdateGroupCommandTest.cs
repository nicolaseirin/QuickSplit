using System;
using System.Linq;
using System.Threading;
using System.Collections.Generic;
using QuickSplit.Domain;
using Xunit;
using System.Text;
using QuickSplit.Application.Groups.Commands;

namespace QuickSplit.Test.Application
{
    public class UpdateGroupCommandTest: CommandsTestBase
    {
        public UpdateGroupCommandTest()
        {
            var membership = new Membership()
            {
                User = new User(),
                UserId = 1,
                Group = new Group(),
                GroupId = 3
            };
            membership.UserId = 10;
            var original = new Group()
            {
                Id = 1,
                Name = "John",
                Memberships = new List<Membership>(),
                
            };
            original.Memberships.Add(membership);
            Groups.Add(original);
        }

        [Fact]
        public async void UpdateValidGroupTest()
        {

            var command = new ModifyGroupCommand()
            {
                Id = 1,
                Name = "The pedrera",
                Memberships = new List<int>()
            };
            command.Memberships.Add(10);
            var handler = new UpdateGroupCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);
            Group group = Groups.Single(g => g.Id == 1);

            Assert.Equal(command.Name, group.Name);
            Assert.Equal(command.Memberships.Count, group.Memberships.Count);
            Assert.True(group.Memberships.Select(m => m.GroupId == 1 && m.UserId == 10) != null);
        }
    }
}

