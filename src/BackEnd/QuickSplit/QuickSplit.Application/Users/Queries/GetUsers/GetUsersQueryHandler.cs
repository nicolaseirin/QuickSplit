using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries.GetUsers
{
    public class GetUsersQueryHandler : IRequestHandler<GetUsersQuery, IEnumerable<UserModel>>
    {
        private IQuickSplitContext context;

        public GetUsersQueryHandler(IQuickSplitContext context)
        {
            this.context = context;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetUsersQuery request, CancellationToken cancellationToken)
        {
            return await context.
                Users.
                Select(user => MapToModel(user))
                .ToListAsync();
        }

        private UserModel MapToModel(User user)
        {
            return new UserModel()
            {
                Id = user.Id,
                Name = user.Name,
                LastName = user.LastName,
                Telephone = user.Telephone,
                Mail = user.Mail
            };
        }
    }
}