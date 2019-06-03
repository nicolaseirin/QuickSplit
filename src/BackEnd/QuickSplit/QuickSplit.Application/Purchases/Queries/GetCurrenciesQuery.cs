using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Queries
{
    public class GetCurrenciesQueryHandler : IRequestHandler<GetCurrenciesQuery, IEnumerable<string>>
    {
        public async Task<IEnumerable<string>> Handle(GetCurrenciesQuery request, CancellationToken cancellationToken)
        {
            return Enum.GetValues(typeof(Currency)).Cast<Currency>().Select(currency => currency.ToString());
        }
    }

    public class GetCurrenciesQuery : IRequest<IEnumerable<string>>
    {
        
    }
}