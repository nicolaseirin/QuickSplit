using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
{
    public class GetUserByIdQueryHandler : IRequestHandler<GetUserByIdQuery, UserModel>
    {
        private readonly IQuickSplitContext _context;
        private readonly IAvatarRepository _avatarRepository;
        private readonly DbSet<User> _users;
        

        public GetUserByIdQueryHandler(IQuickSplitContext context, IAvatarRepository avatarRepository)
        {
            _context = context;
            _avatarRepository = avatarRepository;
            _users = context.Users;
        }

        public async Task<UserModel> Handle(GetUserByIdQuery request, CancellationToken cancellationToken)
        {
            User user = await _users.FindAsync(request.Id);
            
            if(user == null)
                throw new InvalidQueryException($"No existe usuario con id {request.Id}.");

            string avatar = await _avatarRepository.GetAvatarBase64(request.Id);
            
            return new UserModel(user, avatar);
        }
    }
    
    public class GetUserByIdQuery : IRequest<UserModel>
    {
        public int Id { get; set; }
    }
}