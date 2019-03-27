using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.CreateUser
{
    public class CreateUserCommandHandler : IRequestHandler<CreateUserCommand, Unit>
    {
        private readonly IQuickSplitContext context;

        public CreateUserCommandHandler(IQuickSplitContext context)
        {
            this.context = context;
        }

        public async Task<Unit> Handle(CreateUserCommand request, CancellationToken cancellationToken)
        {
            User toCreate = new User()
            {
                Name = request.Name,
                LastName = request.LastName,
                Mail = request.Mail,
                Telephone = request.Telephone,
                Password = request.Password
            };

            await context.Users.AddAsync(toCreate);
            await context.SaveChangesAsync();
            
            return Unit.Value;
        }
    }
}