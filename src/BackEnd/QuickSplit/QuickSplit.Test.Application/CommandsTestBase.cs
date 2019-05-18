using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Test.Application
{
    public abstract class CommandsTestBase
    {
        protected IQuickSplitContext Context;
        protected IPasswordHasher PasswordHasher;
        protected DbSet<User> Users;
        protected DbSet<Friendship> Friendships;

        protected CommandsTestBase()
        {
            Context = TestingFactory.CreateInMemoryContext();
            Users = Context.Users;
            Friendships = Context.Friendships;
            PasswordHasher = new PasswordHasher.PasswordHasher();
        }
    }
}