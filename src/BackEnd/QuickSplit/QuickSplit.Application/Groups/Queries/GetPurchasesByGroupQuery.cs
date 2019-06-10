using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetPurchasesByGroupQueryHandler : IRequestHandler<GetPurchasesByGroupQuery, IEnumerable<PurchaseModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetPurchasesByGroupQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
        }

        public async Task<IEnumerable<PurchaseModel>> Handle(GetPurchasesByGroupQuery request, CancellationToken cancellationToken)
        {
            Group group = await _context
                              .Groups
                              .Include(group1 => group1.Purchases)
                              .ThenInclude(purchase => purchase.Group)
                              .ThenInclude(group1 => group1.Purchases)
                              .ThenInclude(purchase => purchase.Participants)
                              .ThenInclude(participant => participant.Purchase.Purchaser)
                              .FirstOrDefaultAsync(group1 => group1.Id == request.GroupId, cancellationToken: cancellationToken)
                          ?? throw new InvalidQueryException($"No existe el grupo");

            return group.Purchases.Select(purchase => new PurchaseModel(purchase));
        }
    }

    public class GetPurchasesByGroupQuery : IRequest<IEnumerable<PurchaseModel>>
    {
        public int GroupId { get; set; }
    }
}