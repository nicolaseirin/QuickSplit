using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Commands
{
    public class CreatePurchaseWithQrCommandHandler : IRequestHandler<CreatePurchaseWithQrCommand, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;
        private readonly IQrTickerReader _qrTickerReader;

        public CreatePurchaseWithQrCommandHandler(IQuickSplitContext context, IQrTickerReader qrTickerReader)
        {
            _context = context;
            _qrTickerReader = qrTickerReader;
        }

        public async Task<PurchaseModel> Handle(CreatePurchaseWithQrCommand request, CancellationToken cancellationToken)
        {
            if (string.IsNullOrWhiteSpace(request.Name))
                throw new InvalidCommandException($"Nombre de la compra {request.Name} es invalido");
            
            Group group = await GetGroupIfValid(request);
            User purchaser = await GetPurchaserIfValid(request);
            IEnumerable<User> participants = await GetParticipantsIfValid(request.Participants, group);
            (double cost, Currency currency) = await _qrTickerReader.ReadTicket(request.QrResult);

            var purchase = new Purchase(purchaser, group, cost, currency, participants, request.Name, request.Longitude, request.Latitude);
            group.Purchases.Add(purchase);
            await _context.SaveChangesAsync();

            return new PurchaseModel(purchase);
        }
        
        private async Task<User[]> GetParticipantsIfValid(IEnumerable<int> requestParticipants, Group @group)
        {
            User[] participants = await Task.WhenAll(requestParticipants.Select(u => _context.Users.FindAsync(u)));
            if (participants.Any(u => u == null)) 
                throw new InvalidCommandException("Participantes invalidos");

            return participants;
        }

        private async Task<User> GetPurchaserIfValid(CreatePurchaseCommand request)
        {
            return await _context.Users.FindAsync(request.Purchaser) ?? throw new InvalidCommandException("El comprador no existe");
        }

        private async Task<Group> GetGroupIfValid(CreatePurchaseCommand request)
        {
            return await _context.Groups.FindAsync(request.Group) ?? throw new InvalidCommandException("Grupo no existe");
        }
    }

    public class CreatePurchaseWithQrCommand : CreatePurchaseCommand, IRequest<PurchaseModel>
    {
        public string QrResult { get; set; }
    }
}