using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class DeleteUserCommandHandler : IRequestHandler<DeleteUserCommand>
    {
        private readonly IQuickSplitContext _context;
        private readonly IAvatarRepository _avatarRepository;

        public DeleteUserCommandHandler(IQuickSplitContext context, IAvatarRepository avatarRepository)
        {
            _context = context;
            _avatarRepository = avatarRepository;
        }

        public async Task<Unit> Handle(DeleteUserCommand request, CancellationToken cancellationToken)
        {
            User toDelete = await _context.Users.FindAsync(request.Id);
            IEnumerable<Friendship> friendshipsToDelete = _context.Friendships.Where(friendship => friendship.Friend1Id == toDelete.Id || friendship.Friend2Id == toDelete.Id);

            if (toDelete == null) return Unit.Value;
            
            _context.Friendships.RemoveRange(friendshipsToDelete);
            _context.Users.Remove(toDelete);
            _avatarRepository.DeleteImage(request.Id);
            await _context.SaveChangesAsync();

            return Unit.Value;
        }
    }
    
    public class DeleteUserCommand : IRequest
    {
        public int Id { get; set; }
    }
}