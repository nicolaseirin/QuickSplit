using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetGroupByIdQueryHandler: IRequestHandler<GetGroupByIdQuery, GroupModel>
    {
        private readonly IQuickSplitContext _context;

        public GetGroupByIdQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }


        public async Task<GroupModel> Handle(GetGroupByIdQuery request, CancellationToken cancellationToken)
        {
            Group group = await _context.Groups
                .Include("Admin")
                .Include("Memberships")
                .Include("Purchases")
                .FirstOrDefaultAsync(g => g.Id == request.Id, cancellationToken: cancellationToken);
            
            
            if (group == null)
                throw new InvalidQueryException($"No existe grupo con id {request.Id}.");

            return new GroupModel(group);
        }
    }
    
    public class GetGroupByIdQuery: IRequest<GroupModel>
    {
        public int Id { get; set; }
    }
}








