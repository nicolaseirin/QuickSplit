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
    public class GetPurchasesByGroupQueryHandler : IRequestHandler<GetPurchasesByGroupQuery, IEnumerable<PurchaseModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetPurchasesByGroupQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<PurchaseModel>> Handle(GetPurchasesByGroupQuery request, CancellationToken cancellationToken)
        {
            Group group = await _context.Groups.FindAsync(request.GroupId) ?? throw new InvalidQueryException($"No existe el grupo");

            return await Task.WhenAll(group.Purchases.Select(MapPurchase));
        }

        private async Task<PurchaseModel> MapPurchase(Purchase purchase)
        {
            string image = await _imageRepository.GetImageBase64(purchase.Id);
            return new PurchaseModel(purchase, image);
        }
    }

    public class GetPurchasesByGroupQuery : IRequest<IEnumerable<PurchaseModel>>
    {
        public int GroupId { get; set; }
    }
}