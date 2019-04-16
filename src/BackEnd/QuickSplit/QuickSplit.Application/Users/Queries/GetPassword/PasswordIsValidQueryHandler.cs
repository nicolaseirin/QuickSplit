using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries.GetPassword
{
    public class PasswordIsValidQueryHandler : IRequestHandler<PasswordIsValidQuery, bool>
    {
        private readonly IQuickSplitContext _context;
        private readonly IPasswordHasher _hasher;
        private readonly DbSet<User> _users;
        

        public PasswordIsValidQueryHandler(IQuickSplitContext context, IPasswordHasher hasher)
        {
            this._context = context;
            _users = context.Users;
            _hasher = hasher;
        }

        public async Task<bool> Handle(PasswordIsValidQuery request, CancellationToken cancellationToken)
        {
            User user = await _users.FindAsync(request.Id);
            
            string hashedPassword = _hasher.Hash(request.Password);

            return user != null &&  hashedPassword == user.Password;
        }
    }
}