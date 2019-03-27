using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;
using MediatR;

namespace QuickSplit.Application.Values.Queries.GetValues
{
    public class GetValuesQueryHandler : IRequestHandler<GetValuesQuery, IEnumerable<string>>
    {
        public GetValuesQueryHandler() // Aca podria recibir elementos que fueron inyectado por inversion de dependencia como por ejemplo el contexto de la base o el IRepository
        {
        }


        public async Task<IEnumerable<string>> Handle(GetValuesQuery request, CancellationToken cancellationToken)
        {
            
            return await GetValuesFromDataBase();
        }

        private async Task<IEnumerable<string>> GetValuesFromDataBase() // Esto seria la llamada al EntityFramework o al IRepo
        {
            return await Task.Run(() => new[] {"Vamo", "Y", "Vamos"});
        }
    }
}