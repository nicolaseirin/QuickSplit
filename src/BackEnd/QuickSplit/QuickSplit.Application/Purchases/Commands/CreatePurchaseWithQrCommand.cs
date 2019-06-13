using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Purchases.Commands;
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Commands
{
    public class CreatePurchaseWithQrCommandHandler : IRequestHandler<CreatePurchaseWithQrCommand, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;
        private readonly ITicketReader _ticketReader;

        public CreatePurchaseWithQrCommandHandler(IQuickSplitContext context, ITicketReader ticketReader)
        {
            _context = context;
            _ticketReader = ticketReader;
        }

        public async Task<PurchaseModel> Handle(CreatePurchaseWithQrCommand request, CancellationToken cancellationToken)
        {
            if (string.IsNullOrWhiteSpace(request.Name))
                throw new InvalidCommandException($"Nombre de la compra {request.Name} es invalido");
            Group group = await GetGroupIfValid(request);
            User purchaser = await GetPurchaserIfValid(request);
            IEnumerable<User> participants = await GetParticipantsIfValid(request.Participants, group);
            (Currency, double) costWithCurrency = _ticketReader.ReadTicket(request.QrImage);

            var purchase = new Purchase(purchaser, group, costWithCurrency.Item2, costWithCurrency.Item1, participants, request.Name, request.Longitude, request.Latitude);
            group.Purchases.Add(purchase);
            await _context.SaveChangesAsync();


            return new PurchaseModel(purchase);
        }

        private static Currency GetCurrencyIfValid(CreatePurchaseCommand request)
        {
            bool currencyIsValid = Enum.TryParse(request.Currency, out Currency currency);
            if (!currencyIsValid)
                throw new InvalidCommandException($"{request.Currency} no es una moneda valida");
            return currency;
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
        public Stream QrImage { get; set; }
    }
}

