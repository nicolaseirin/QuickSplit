using System;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
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
            User user = await _users.FirstOrDefaultAsync(u => string.Equals(u.Mail, request.Mail, StringComparison.OrdinalIgnoreCase));
            string hashedPassword = _hasher.Hash(request.Password);

            return user != null &&  hashedPassword == user.Password;
        }
    }
    
    public class PasswordIsValidQuery : IRequest<bool>
    {
        public string Mail { get; set; }
        
        public string Password { get; set; }
    }
}