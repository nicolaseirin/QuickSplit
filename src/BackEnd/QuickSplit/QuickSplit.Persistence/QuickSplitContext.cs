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
        public DbSet<Group> Groups { get; set; }
        public DbSet<Membership> Memberships { get; set; }

        public async void SaveChanges()
        {
            await SaveChangesAsync();
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

            modelBuilder.Entity<Group>()
                .Property(group => group.Id)
                .ValueGeneratedOnAdd();

            modelBuilder.Entity<User>()
                .HasIndex(user => user.Mail)
                .IsUnique();

            modelBuilder.Entity<Membership>()
                .HasKey(membership => new { membership.UserId, membership.GroupId });
        }

    }
}