using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Test.Application
{
    public class CommandsTestBase
    {
        protected readonly IQuickSplitContext _context;
        protected readonly DbSet<User> _users;

        public CommandsTestBase()
        {
            _context = TestingFactory.CreateInMemoryContext();
            _users = _context.Users;
        }
    }
}