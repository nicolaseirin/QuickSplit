using System.Linq;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
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
        public DbSet<Friendship> Friendships { get; set; }

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
            ConfigureUser(modelBuilder.Entity<User>());

            ConfigureFriendship(modelBuilder.Entity<Friendship>());
        }

        private void ConfigureFriendship(EntityTypeBuilder<Friendship> builder)
        {
            builder.HasKey(f => new {f.Friend1Id, f.Friend2Id});
            
            
            builder
                .HasOne(u => u.Friend1)
                .WithMany(user => user.FriendsOf)
                .HasForeignKey(friendship => friendship.Friend1Id)
                .OnDelete(DeleteBehavior.ClientSetNull);

            builder
                .HasOne(u => u.Friend2)
                .WithMany(user => user.Friends)
                .HasForeignKey(friendship => friendship.Friend2Id)
                .OnDelete(DeleteBehavior.ClientSetNull);
        }

        private void ConfigureUser(EntityTypeBuilder<User> builder)
        {
            builder
                .Property(user => user.Id)
                .ValueGeneratedOnAdd();

            builder
                .HasMany(user => user.Friends)
                .WithOne(friendship => friendship.Friend2);
                //.OnDelete(DeleteBehavior.ClientSetNull);
            
            builder
                .HasMany(user => user.FriendsOf)
                .WithOne(friendship => friendship.Friend1);
                //.OnDelete(DeleteBehavior.ClientSetNull);

            builder
                .HasIndex(user => user.Mail)
                .IsUnique();
        }
    }
}