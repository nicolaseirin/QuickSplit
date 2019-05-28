using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using Microsoft.EntityFrameworkCore;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Queries
{
    public class GetPurchasesByUserQueryHandler : IRequestHandler<GetPurchasesByUserQuery, IEnumerable<PurchaseModel>>
    {
        private readonly IQuickSplitContext _context;

        public GetPurchasesByUserQueryHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<PurchaseModel>> Handle(GetPurchasesByUserQuery request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidQueryException("Usuario no existe");

            List<Participant> participants = await _context
                .Participants
                .Where(participant => participant.UserId == user.Id)
                .Include(participant => participant.Purchase)
                .ToListAsync(cancellationToken: cancellationToken);
            
            return participants
                .Select(participant =>  new PurchaseModel(participant.Purchase))
                .ToList();
        }
    }

    public class GetPurchasesByUserQuery : IRequest<IEnumerable<PurchaseModel>>
    {
        public int UserId { get; set; }
    }
}