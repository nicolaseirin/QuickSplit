using System;
using System.Collections.Generic;
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
    public class CreateAddPurchaseCommandHandler : IRequestHandler<CreatePurchaseCommand, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;

        public CreateAddPurchaseCommandHandler(IQuickSplitContext context)
        {
            _context = context; ;
        }

        public async Task<PurchaseModel> Handle(CreatePurchaseCommand request, CancellationToken cancellationToken)
        {
            if (string.IsNullOrWhiteSpace(request.Name))
                throw new InvalidCommandException($"Nombre de la compra {request.Name} es invalido");
            Group group = await GetGroupIfValid(request);
            User purchaser = await GetPurchaserIfValid(request);
            IEnumerable<User> participants = await GetParticipantsIfValid(request.Participants, group);
            Currency currency = GetCurrencyIfValid(request);

            var purchase = new Purchase(purchaser, group, request.Cost, currency, participants, request.Name, request.Longitude, request.Latitude);
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

    public class CreatePurchaseCommand : IRequest<PurchaseModel>
    {
        public int Purchaser { get; set; }

        public int Group { get; set; }
        
        public string Name { get; set; }

        public IEnumerable<int> Participants { get; set; }

        public double Cost { get; set; }

        public string Currency { get; set; }

        public double Longitude { get; set; }

        public double Latitude { get; set; }
    }
}