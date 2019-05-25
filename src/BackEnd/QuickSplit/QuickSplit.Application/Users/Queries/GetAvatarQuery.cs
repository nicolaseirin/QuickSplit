using System;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore.Query.Internal;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.Application.Users.Queries
{
    public class GetAvatarQueryHandler : IRequestHandler<GetAvatarQuery, Stream>
    {
        private readonly IQuickSplitContext _context;
        private readonly IAvatarRepository _avatarRepository;

        public GetAvatarQueryHandler(IQuickSplitContext context, IAvatarRepository avatarRepository)
        {
            _context = context;
            _avatarRepository = avatarRepository;
        }

        public async Task<Stream> Handle(GetAvatarQuery request, CancellationToken cancellationToken)
        {
            if (await _context.Users.FindAsync(request.UserId) == null)
                throw new InvalidQueryException("No existe el usuario");

            return _avatarRepository.GetAvatarStream(request.UserId);
        }
    }

    public class GetAvatarQuery : IRequest<Stream>
    {
        public int UserId { get; set; }
    }
}