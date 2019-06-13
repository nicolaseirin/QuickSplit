using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetSplitCostReportHandler : IRequestHandler<GetSplitCostReportQuery, IEnumerable<DebtorDebteeModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetSplitCostReportHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<DebtorDebteeModel>> Handle(GetSplitCostReportQuery request, CancellationToken cancellationToken)
        {
            Group @group = await _context.Groups.FindAsync(request.GroupId) ?? throw new InvalidQueryException("No existe el grupo");

            return group
                    .GenerateSplitCostReport()
                    .Dictionary
                    .Select(MapDebtorDebtee);
        }

        private DebtorDebteeModel MapDebtorDebtee(KeyValuePair<(User, User), double> pair)
        {
            return new DebtorDebteeModel()
            {
                Amount = pair.Value,
                Debtor = new UserModel(pair.Key.Item1),
                Debtee = new UserModel(pair.Key.Item2)
            };
        }
    }

    public class GetSplitCostReportQuery : IRequest<IEnumerable<DebtorDebteeModel>>
    {
        public int GroupId { get; set; }
    }
}