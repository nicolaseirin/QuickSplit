using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.CreateUser
{
    public class CreateUserCommandHandler : IRequestHandler<CreateUserCommand, UserModel>
    {
        private readonly IQuickSplitContext _context;
        private readonly IPasswordHasher _hasher;

        public CreateUserCommandHandler(IQuickSplitContext context, IPasswordHasher hasher)
        {
            _context = context;
            _hasher = hasher;
        }

        public async Task<UserModel> Handle(CreateUserCommand request, CancellationToken cancellationToken)
        {
            UserModel response = null;
            try
            {
                response = await TryToHandle(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }

            return response;
        }

        private async Task<UserModel> TryToHandle(CreateUserCommand request)
        {
            var toCreate = new User()
            {
                Name = request.Name,
                LastName = request.LastName,
                Mail = request.Mail,
                Password = _hasher.Hash(request.Password)
            };

            await _context.Users.AddAsync(toCreate);
            await _context.SaveChangesAsync();

            return new UserModel(toCreate);
        }
    }
}