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
        private readonly IQuickSplitContext context;

        public CreateUserCommandHandler(IQuickSplitContext context)
        {
            this.context = context;
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
                Password = request.Password
            };

            await context.Users.AddAsync(toCreate);
            await context.SaveChangesAsync();

            return new UserModel(toCreate);
        }
    }
}