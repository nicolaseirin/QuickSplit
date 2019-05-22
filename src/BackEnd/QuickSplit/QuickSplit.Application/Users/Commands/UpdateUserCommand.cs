using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands
{
    public class UpdateUserCommandHandler : IRequestHandler<UpdateUserCommand, UserModel>
    {
        private readonly IQuickSplitContext _context;

        public UpdateUserCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<UserModel> Handle(UpdateUserCommand request, CancellationToken cancellationToken)
        {
            try
            {
                return await TryToUpdate(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }
        }

        private async Task<UserModel> TryToUpdate(UpdateUserCommand request)
        {
            int id = request.Id;
            User toUpdate = await _context.Users.FindAsync(id);

            toUpdate.Name = request.Name ?? toUpdate.Name;
            toUpdate.LastName = request.LastName ?? toUpdate.LastName;
            toUpdate.Mail = request.Mail ?? toUpdate.Mail;
            if (!string.IsNullOrEmpty(request.Password))
                toUpdate.Password = request.Password;
            
            await _context.SaveChangesAsync();

            return new UserModel(toUpdate);
        }
    }

    public class UpdateUserCommand : IRequest<UserModel>
    {
        public string Name { get; set; }

        public int Id { get; set; }

        public string LastName { get; set; }

        public string Mail { get; set; }

        public string Password { get; set; }
    }
}