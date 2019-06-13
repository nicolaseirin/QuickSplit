using System.IO;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Commands
{
    public class AddPurchaseImageCommandHandler : IRequestHandler<AddPurchaseImageCommand, Unit>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public AddPurchaseImageCommandHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
            _imageRepository.FolderName = "Purchases";
            _imageRepository.ImageQualityRatio = 50;
        }

        public async Task<Unit> Handle(AddPurchaseImageCommand request, CancellationToken cancellationToken)
        {
            Purchase purchase = await _context.Purchases.FindAsync(request.PurchaseId) ?? throw new InvalidCommandException("No existe la compra");
            
            _imageRepository.AddImageFromStream(request.PurchaseId, request.Image);
            
            return Unit.Value;
        }
    }

    public class AddPurchaseImageCommand : IRequest
    {
        public int PurchaseId { get; set; }
        
        public Stream Image { get; set; }
    }
}