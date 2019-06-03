using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetSplitCostReportHandler : IRequestHandler<GetSplitCostReportQuery, IEnumerable<DebtorDebteeModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetSplitCostReportHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<DebtorDebteeModel>> Handle(GetSplitCostReportQuery request, CancellationToken cancellationToken)
        {
            Group @group = await _context.Groups.FindAsync(request.GroupId) ?? throw new InvalidQueryException("No existe el grupo");

            return group.GenerateSplitCostReport().Dictionary.Select(pair => new DebtorDebteeModel()
                {
                    Amount = pair.Value,
                    Debtor = pair.Key.Item1.Id,
                    Debtee = pair.Key.Item2.Id
                })
                .ToList();
        }
    }

    public class GetSplitCostReportQuery : IRequest<IEnumerable<DebtorDebteeModel>>
    {
        public int GroupId { get; set; }
    }
}