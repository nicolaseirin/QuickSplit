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

namespace QuickSplit.Application.Purchases.Queries
{
    public class GetPurchaseMembersQueryHandler : IRequestHandler<GetPurchaseParticipantsQuery, IEnumerable<UserModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetPurchaseMembersQueryHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<UserModel>> Handle(GetPurchaseParticipantsQuery request, CancellationToken cancellationToken)
        {
            Purchase purchase = await _context
                                    .Purchases
                                    .Include(p => p.Participants)
                                    .ThenInclude(p => p.User)
                                    .FirstOrDefaultAsync(r => r.Id == request.PurchaseId, cancellationToken: cancellationToken) 
                                ?? throw new InvalidQueryException("No existe la compra");

            return await Task.WhenAll(
                purchase
                    .Participants
                    .Select(p => MapUser(p.User))
                );
        }
        
        private async Task<UserModel> MapUser(User user)
        {
            string image = await _imageRepository.GetImageBase64(user.Id);

            return new UserModel(user, image);
        }
    }

    public class GetPurchaseParticipantsQuery : IRequest<IEnumerable<UserModel>>
    {
        public int PurchaseId { get; set; }
    }
}