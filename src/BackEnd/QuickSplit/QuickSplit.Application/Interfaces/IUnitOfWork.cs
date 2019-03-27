using System;
using QuickSplit.Domain;

namespace QuickSplit.Application.Interfaces
{
    public interface IUnitOfWork : IDisposable
    {
        IRepository<User> Users { get; }

        void Save();
    }
}