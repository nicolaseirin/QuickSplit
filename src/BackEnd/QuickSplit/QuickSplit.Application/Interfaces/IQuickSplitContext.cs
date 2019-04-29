using System;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Domain;

namespace QuickSplit.Application.Interfaces
{
    public interface IQuickSplitContext : IDisposable
    {
        DbSet<User> Users { get; }
        DbSet<Group> Groups { get; }
        DbSet<Domain.Membership> Memberships { get; }

        void SaveChanges();
        Task SaveChangesAsync();
    }
}