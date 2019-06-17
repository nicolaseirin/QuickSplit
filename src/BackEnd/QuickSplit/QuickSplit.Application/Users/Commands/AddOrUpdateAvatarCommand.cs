using System;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class AddOrUpdateAvatarCommandHandler : IRequestHandler<AddOrUpdateAvatarCommand, Unit>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public AddOrUpdateAvatarCommandHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<Unit> Handle(AddOrUpdateAvatarCommand request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidCommandException("No existe el usuario");
            if (request.Compression != null)
                _imageRepository.ImageQualityRatio = request.Compression.Value;

            _imageRepository.AddImageFromStream(request.UserId, request.ImageStream);

            return Unit.Value;
        }
    }

    public class AddOrUpdateAvatarCommand : IRequest
    {
        public Stream ImageStream { get; set; }

        public int UserId { get; set; }
        
        public int? Compression { get; set; }
    }
}