using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace QuickSplit.Application.Interfaces
{
    public interface IRepository<T> : IQueryable<T> where T : class
    {
        void Delete(T toDelete);

        void DeleteRange(IEnumerable<T> toDelete);

        void Insert(T toInsert);
        
        void InsertRange(IEnumerable<T> toInsert);
    }
}