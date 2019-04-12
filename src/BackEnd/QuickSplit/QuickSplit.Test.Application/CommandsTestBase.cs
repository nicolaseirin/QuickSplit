using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Test.Application
{
    public abstract class CommandsTestBase
    {
        protected readonly IQuickSplitContext _context;
        protected readonly DbSet<User> _users;

        protected CommandsTestBase()
        {
            _context = TestingFactory.CreateInMemoryContext();
            _users = _context.Users;
        }
    }
}