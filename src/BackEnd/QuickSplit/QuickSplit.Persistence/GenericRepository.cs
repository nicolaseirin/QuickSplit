using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.Persistence
{
    public class GenericRepository<T> : IRepository<T> where T : class
    {
        private readonly DbSet<T> set;
        private readonly IQueryable<T> queryable;

        public GenericRepository(DbSet<T> set)
        {
            this.set = set;
            queryable = set;
        }

        public IEnumerator<T> GetEnumerator()
        {
            return queryable.GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }

        public Type ElementType => queryable.ElementType;
        public Expression Expression => queryable.Expression;
        public IQueryProvider Provider => queryable.Provider;

        public void Delete(T toDelete)
        {
            set.Remove(toDelete);
        }

        public void DeleteRange(IEnumerable<T> toDelete)
        {
            set.RemoveRange(toDelete);
        }

        public async void Insert(T toInsert)
        {
            await set.AddAsync(toInsert);
        }

        public async void InsertRange(IEnumerable<T> toInsert)
        {
            await set.AddRangeAsync(toInsert);
        }
    }
}