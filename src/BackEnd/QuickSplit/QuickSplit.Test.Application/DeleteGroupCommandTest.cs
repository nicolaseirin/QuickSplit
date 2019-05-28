using System;
using System.Collections.Generic;
using System.Threading;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Domain;
using Xunit;


namespace QuickSplit.Test.Application
{
    public class DeleteGroupCommandTest :CommandsTestBase
    {
        [Fact]
        public void DeleteNonExistingGroupTest()
        {
            Groups.Add(new Group()
            {
                Id = 1,
                Name = "La pedrera",
                Memberships = new List<Membership>()
            });
            Context.SaveChanges();
            var command = new DeleteGroupCommand()
            {
                Id = 4
            };
            var handler = new DeleteGroupCommandHandler(Context);

            Assert.Throws<AggregateException>(() => handler.Handle(command, CancellationToken.None).Result);
            Assert.Single(Groups);
        }

        [Fact]
        public async void DeleteExistingGroupTest()
        {
            var group = new Group()
            {
                Id = 4,
                Name = "john",
                Memberships = new List<Membership>()
            };
            var mem = new Membership()
            {
                Group = group,
                User = new User(),
                GroupId = 4,
                UserId = 2,
            };
            group.Memberships.Add(mem);
            Memberships.Add(mem);
            Groups.Add(group);

            var command = new DeleteGroupCommand()
            {
                Id = 4
            };
            var handler = new DeleteGroupCommandHandler(Context);

            await handler.Handle(command, CancellationToken.None);

            Assert.Empty(Memberships);
            Assert.Empty(Groups);
        }
    }
}

