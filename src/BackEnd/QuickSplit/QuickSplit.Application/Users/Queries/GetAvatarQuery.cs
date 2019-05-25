using System;
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
            string basePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Avatars");
            string imagePath = Directory
                .GetFiles(basePath)
                .FirstOrDefault(path => IsFileWithName(request.UserId.ToString(), path))
                ?? basePath + "/default.png";

            return new FileStream(imagePath, FileMode.Open);
        }

        private static bool IsFileWithName(string fileName, string filePath)
        {
            return filePath
                .Split('/')
                .Last()
                .Split('.')
                .First()
                .Equals(fileName, StringComparison.OrdinalIgnoreCase);
        }
    }

    public class GetAvatarQuery : IRequest<Stream>
    {
        public int UserId { get; set; }
    }
}