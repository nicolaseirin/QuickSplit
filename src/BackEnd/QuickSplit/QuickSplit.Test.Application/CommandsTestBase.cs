using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;
using Remotion.Linq.Parsing.Structure.IntermediateModel;

namespace QuickSplit.Test.Application
{
    public abstract class CommandsTestBase
    {
        protected readonly IQuickSplitContext Context;
        protected readonly DbSet<User> Users;
        protected readonly DbSet<Group> Groups;
        protected readonly IPasswordHasher PasswordHasher;
        protected readonly DbSet<Friendship> Friendships;
        protected readonly DbSet<Membership> Memberships;

        protected CommandsTestBase()
        {
            Context = TestingFactory.CreateInMemoryContext();
            Users = Context.Users;
            Friendships = Context.Friendships;
            Groups = Context.Groups;
            Memberships = Context.Memberships;
            PasswordHasher = new PasswordHasher.PasswordHasher();
        }
    }
}