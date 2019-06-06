using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.Application.Purchases.Queries
{
    public class GetPurchasesQueryHandler : IRequestHandler<GetPurchasesQuery, IEnumerable<PurchaseModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetPurchasesQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<PurchaseModel>> Handle(GetPurchasesQuery request, CancellationToken cancellationToken)
        {
            return await _context
                .Purchases
                .Include(purchase => purchase.Group)
                .ThenInclude(group => group.Purchases)
                .Include(group => group.Participants)
                .Include(purchase => purchase.Purchaser)
                .Select(purchase => new PurchaseModel(purchase))
                .ToListAsync(cancellationToken);
        }
    }

    public class GetPurchasesQuery : IRequest<IEnumerable<PurchaseModel>>
    {
        
    }
}