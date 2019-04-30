using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Persistence
{
    public class QuickSplitContext : DbContext, IQuickSplitContext
    {
        public QuickSplitContext(DbContextOptions options) : base(options)
        {
            Database.EnsureCreated();
        }

        public DbSet<User> Users { get; set; }

        public void SaveChanges()
        {
            base.SaveChanges();
        }
        
        public async Task SaveChangesAsync()
        {
            await base.SaveChangesAsync();
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>()
                .Property(user => user.Id)
                .ValueGeneratedOnAdd();

            modelBuilder.Entity<User>()
                .HasIndex(user => user.Mail)
                .IsUnique();
        }

    }
}