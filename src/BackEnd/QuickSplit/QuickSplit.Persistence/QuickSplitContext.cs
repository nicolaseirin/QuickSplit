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
        public DbSet<Group> Groups { get; set; }
        public DbSet<Membership> Memberships { get; set; }

        public DbSet<Participant> Participants { get; set; }
        public DbSet<Purchase> Purchases { get; set; }

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

            ConfigureGroup(modelBuilder);
        }

        private void ConfigureGroup(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Group>()
                .Property(group => @group.Id)
                .ValueGeneratedOnAdd();

            modelBuilder.Entity<Group>()
                .HasOne(g => g.Admin)
                .WithMany();

            modelBuilder.Entity<Group>()
                .HasMany(g => g.Purchases)
                .WithOne(purchase => purchase.Group);

            modelBuilder.Entity<Group>()
                .HasMany(p => p.Memberships)
                .WithOne(membership => membership.Group);

            modelBuilder.Entity<Membership>()
                .HasKey(membership => new {membership.UserId, membership.GroupId});

            modelBuilder.Entity<Membership>()
                .HasOne(m => m.Group);

            modelBuilder.Entity<Participant>()
                .HasKey(participant => new {participant.UserId, participant.PurchaseId});
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
                .HasMany<Group>()
                .WithOne(group => group.Admin);
                //.OnDelete(DeleteBehavior.Cascade);

            builder
                .HasMany(user => user.Friends)
                .WithOne(friendship => friendship.Friend2);
            //.OnDelete(DeleteBehavior.ClientSetNull);

            builder
                .HasMany(user => user.FriendsOf)
                .WithOne(friendship => friendship.Friend1);
            //.OnDelete(DeleteBehavior.ClientSetNull);
            builder
                .HasMany<Participant>()
                .WithOne(participant => participant.User);

            builder
                .HasMany<Membership>()
                .WithOne(membership => membership.User);
        }
    }
}