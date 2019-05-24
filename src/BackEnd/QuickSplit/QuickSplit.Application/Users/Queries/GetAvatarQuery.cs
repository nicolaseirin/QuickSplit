using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.Application.Users.Queries
{
    public class GetAvatarQueryHandler : IRequestHandler<GetAvatarQuery, Stream>
    {
        private readonly IQuickSplitContext _context;

        public GetAvatarQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Stream> Handle(GetAvatarQuery request, CancellationToken cancellationToken)
        {
            string basePath = Path.Combine(Directory.GetCurrentDirectory(), "Avatars");
            string imagePath = Directory
                .GetFiles(basePath)
                .FirstOrDefault(f => Enumerable.First<string>(f.Split('.')) == request.UserId.ToString());
            if (imagePath == null)
                throw new InvalidQueryException("No hay foto");

            return new FileStream(imagePath, FileMode.Open);
        }
    }

    public class GetAvatarQuery : IRequest<Stream>
    {
        public int UserId { get; set; }
    }
}