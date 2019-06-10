using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class DeleteUserCommandHandler : IRequestHandler<DeleteUserCommand>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public DeleteUserCommandHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<Unit> Handle(DeleteUserCommand request, CancellationToken cancellationToken)
        {
            User toDelete = await _context.Users.FindAsync(request.Id);
            IEnumerable<Friendship> friendshipsToDelete = _context.Friendships.Where(friendship => friendship.Friend1Id == toDelete.Id || friendship.Friend2Id == toDelete.Id);

            if (_context.Groups.Include(group => group.Admin).Any(g => g.Admin.Id == request.Id)) throw new InvalidCommandException("No te podes borrar porque sos admin de una grupo");
            if (toDelete == null) return Unit.Value;
            
            _context.Friendships.RemoveRange(friendshipsToDelete);
            _context.Users.Remove(toDelete);
            _imageRepository.DeleteImage(request.Id);
            await _context.SaveChangesAsync();

            return Unit.Value;
        }
    }
    
    public class DeleteUserCommand : IRequest
    {
        public int Id { get; set; }
    }
}