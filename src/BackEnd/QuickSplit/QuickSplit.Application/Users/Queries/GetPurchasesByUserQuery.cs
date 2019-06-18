using System;
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
        private readonly IImageRepository _imageRepository;

        public GetPurchasesByUserQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
            _imageRepository.FolderName = "Participants";
        }

        public async Task<IEnumerable<PurchaseModel>> Handle(GetPurchasesByUserQuery request, CancellationToken cancellationToken)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidQueryException("Usuario no existe");

            List<Participant> participants = await _context
                .Participants
                .Where(participant => participant.UserId == user.Id)
                .Include(participant => participant.Purchase)
                .ThenInclude(purchase => purchase.Purchaser)
                .Include(participant => participant.Purchase)
                .ThenInclude(purchase => purchase.Group)
                .Include(participant => participant.Purchase)
                .ThenInclude(purchase => purchase.Participants)
                .ToListAsync(cancellationToken: cancellationToken);

            var purchaser = _context.Users.FirstOrDefaultAsync(u => u.Id == request.UserId) ?? throw new InvalidQueryException("Usuario invalido");
            var p = await _context.Purchases.Where(p1 => p1.Purchaser.Id == request.UserId).ToListAsync();
            
            var ad = p.Select(p1 => new PurchaseModel(p1));    

            var r = participants
                .Where(participant => participant.Purchase.Group != null)
                .Select(participant => new PurchaseModel(participant.Purchase))
                .ToList();

                return r.Concat(ad);
        }
    }


    public class GetPurchasesByUserQuery : IRequest<IEnumerable<PurchaseModel>>
    {
        public int UserId { get; set; }
    }
}