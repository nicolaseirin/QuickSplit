using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Persistence
{
    public class UnitOfWork : IUnitOfWork
    {
        private readonly QuickSplitContext context;

        public UnitOfWork(QuickSplitContext context)
        {
            this.context = context;
            Users = new GenericRepository<User>(context.Users);
        }

        public void Dispose()
        {
            context.Dispose();
        }

        public IRepository<User> Users { get; private set; }
        
        public async void Save()
        {
            context.Users.Where(user => user.Id == 1).SingleAsync();
            await context.SaveChangesAsync();
        }
    }
}