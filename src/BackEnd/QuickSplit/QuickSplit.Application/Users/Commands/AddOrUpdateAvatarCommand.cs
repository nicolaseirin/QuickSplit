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
        private readonly string[] ValidFormats = {"png", "jpeg", "jpg"};
        
        public AddOrUpdateAvatarCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(AddOrUpdateAvatarCommand request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidCommandException("No existe el usuario");
            
            string ext = request.ImageFormat.Split('/').Last();
            if (!FormatIsValid(ext))
            {
                throw new InvalidCommandException("Formato de imagen invalido.");
            }
            
            string avatarsDir  = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Avatars");
            Directory.CreateDirectory(avatarsDir);
            string avatarPath = avatarsDir + $"/{request.UserId}.{ext}";
            using (var fs = new FileStream(avatarPath, FileMode.OpenOrCreate))
            {
                await request.ImageStream.CopyToAsync(fs, cancellationToken);
            }
            
            return Unit.Value;
        }

        private bool FormatIsValid(string requestImageFormat)
        {
            return ValidFormats
                .Any(f => f.Equals(requestImageFormat, StringComparison.OrdinalIgnoreCase));
        }
    }

    public class AddOrUpdateAvatarCommand : IRequest
    {
        public Stream ImageStream { get; set; }
        
        public string ImageFormat { get; set; }
        
        public int UserId { get; set; }
    }
}