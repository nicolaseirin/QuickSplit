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
        protected IPasswordHasher PasswordHasher;

        protected CommandsTestBase()
        {
            Context = TestingFactory.CreateInMemoryContext();
            Users = Context.Users;
            Groups = Context.Groups;
            PasswordHasher = new PasswordHasher.PasswordHasher();
        }
    }
}