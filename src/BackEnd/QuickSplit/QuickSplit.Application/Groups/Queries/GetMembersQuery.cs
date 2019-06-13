using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetMembersQueryHandler : IRequestHandler<GetMembersQuery, IEnumerable<UserModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetMembersQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetMembersQuery request, CancellationToken cancellationToken)
        {
            Group group = await _context
                              .Groups
                              .Include(group1 => group1.Memberships)
                              .ThenInclude(membership => membership.User)
                              .Include(group1 => group1.Memberships)
                              .ThenInclude(membership => membership.Group)
                              .FirstOrDefaultAsync(group1 => group1.Id == request.GroupId, cancellationToken: cancellationToken) 
                          ?? throw new InvalidQueryException("No existe grupo");
            
            List<User> users = group
                .Memberships
                .Select(membership => membership.User)
                .ToList();

            return users.Where(user => user != null).Select(u => new UserModel(u));
        }
        
    }

    public class GetMembersQuery : IRequest<IEnumerable<UserModel>>
    {
        public int GroupId { get; set; }
    }
}