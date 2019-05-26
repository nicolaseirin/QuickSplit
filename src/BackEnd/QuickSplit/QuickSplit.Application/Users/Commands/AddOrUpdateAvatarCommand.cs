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
        private readonly IAvatarRepository _avatarRepository;

        public AddOrUpdateAvatarCommandHandler(IQuickSplitContext context, IAvatarRepository avatarRepository)
        {
            _context = context;
            _avatarRepository = avatarRepository;
        }

        public async Task<Unit> Handle(AddOrUpdateAvatarCommand request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidCommandException("No existe el usuario");

            _avatarRepository.SetAvatarFromStream(request.UserId, request.ImageStream, request.ImageFormat);

            return Unit.Value;
        }
    }

    public class AddOrUpdateAvatarCommand : IRequest
    {
        public Stream ImageStream { get; set; }

        public string ImageFormat { get; set; }

        public int UserId { get; set; }
    }
}