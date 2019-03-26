using System.Collections.Generic;
using MediatR;

namespace QuickSplit.Application.Values.Queries.GetValues
{
    public class GetValuesQuery : IRequest<IEnumerable<string>> // El query esta vacio porque no recibe ningun parametro
    {
        
    }
}