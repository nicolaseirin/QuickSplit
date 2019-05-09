using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Commands.UpdateUser
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
            int id = request.Id;
            User toUpdate = await _context.Users.FindAsync(id);
            
            toUpdate.Name = request.Name ?? toUpdate.Name;
            toUpdate.LastName = request.LastName ?? toUpdate.LastName;
            toUpdate.Password = request.Password ?? toUpdate.Password;
            toUpdate.Mail = request.Mail ?? toUpdate.Mail;

            await _context.SaveChangesAsync();
            
            return new UserModel(toUpdate);
        }
    }
}