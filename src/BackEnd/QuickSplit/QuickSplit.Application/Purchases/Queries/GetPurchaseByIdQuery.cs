using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Queries
{
    public class GetPurchasesByIdQueryHandler : IRequestHandler<GetPurchaseByIdQuery, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;

        public GetPurchasesByIdQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<PurchaseModel> Handle(GetPurchaseByIdQuery request, CancellationToken cancellationToken)
        {
            Purchase purchase = await _context
                                    .Purchases
                                    .Include(purchase1 => purchase1.Purchaser)
                                    .Include(purchase1 => purchase1.Participants)
                                    .Include(purchase1 => purchase1.Group)
                                    .FirstOrDefaultAsync(purchase1 => purchase1.Id == request.Id, cancellationToken: cancellationToken)
                                ?? throw new InvalidQueryException("No existe la compra");

            return new PurchaseModel(purchase);
        }
    }

    public class GetPurchaseByIdQuery : IRequest<PurchaseModel>
    {
        public int Id { get; set; }
    }
}