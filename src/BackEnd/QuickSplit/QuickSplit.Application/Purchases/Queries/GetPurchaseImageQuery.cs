using System.IO;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Queries
{
    public class GetPurchaseImageQueryHandler : IRequestHandler<GetPurchaseImageQuery, Stream>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetPurchaseImageQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
            _imageRepository.FolderName = "Purchases";
        }

        public async Task<Stream> Handle(GetPurchaseImageQuery request, CancellationToken cancellationToken)
        {
            Purchase purchase = await _context.Purchases.FindAsync(request.PurchaseId) ?? throw new InvalidQueryException("No existe la compra");
            return _imageRepository.GetImageStream(request.PurchaseId);
        } 
    }

    public class GetPurchaseImageQuery : IRequest<Stream>
    {
        public int PurchaseId { get; set; }
    }
}