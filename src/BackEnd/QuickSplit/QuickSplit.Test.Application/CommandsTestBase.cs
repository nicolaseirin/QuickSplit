using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Test.Application
{
    public abstract class CommandsTestBase
    {
        protected readonly IQuickSplitContext Context;
        protected readonly DbSet<User> Users;

        protected CommandsTestBase()
        {
            Context = TestingFactory.CreateInMemoryContext();
            Users = Context.Users;
        }
    }
}