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
using QuickSplit.Domain;

namespace QuickSplit.Application.Purchases.Commands
{
    public class ModifyPurchaseCommandHandler : IRequestHandler<ModifyPurchaseCommand, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;

        public ModifyPurchaseCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<PurchaseModel> Handle(ModifyPurchaseCommand request, CancellationToken cancellationToken)
        {
            Purchase purchase = await _context
                                    .Purchases
                                    .Include(purchase1 => purchase1.Group)
                                    .Include(purchase1 => purchase1.Purchaser)
                                    .Include(purchase1 => purchase1.Participants)
                                    .FirstOrDefaultAsync(purchase1 => purchase1.Id == request.PurchaseId, cancellationToken: cancellationToken)
                                ?? throw new InvalidCommandException("Compra no existe");
            
            UpdateNameIfNeeded(request, purchase);
            UpdateCostIfNeeded(request, purchase);
            UpdateCurrencyIfNeeded(request, purchase);
            await UpdateParticipantsIfNeeded(request, purchase);
            await _context.SaveChangesAsync();
            
            return new PurchaseModel(purchase);
        }

        private void UpdateNameIfNeeded(ModifyPurchaseCommand request, Purchase purchase)
        {
            if (!string.IsNullOrWhiteSpace(request.Name))
                purchase.Name = request.Name;
        }

        private async Task UpdateParticipantsIfNeeded(ModifyPurchaseCommand request, Purchase purchase)
        {
            if (request.Participants == null) return;

            User[] users = await GetParticipantsIfValid(request.Participants);
            purchase.Participants = users
                .Select(user => new Participant()
                {
                    Purchase = purchase,
                    PurchaseId = purchase.Id,
                    User = user,
                    UserId = user.Id
                })
                .ToList();
        }

        private async Task<User[]> GetParticipantsIfValid(IEnumerable<int> requestParticipants)
        {
            User[] participants = await Task.WhenAll(requestParticipants.Select(u => _context.Users.FindAsync(u)));
            if (participants.Any(u => u == null))
                throw new InvalidQueryException("Participantes invalidos");

            return participants;
        }
        private static void UpdateCurrencyIfNeeded(ModifyPurchaseCommand request, Purchase purchase)
        {
            if (string.IsNullOrWhiteSpace(request.Currency)) return;

            bool currencyIsValid = Enum.TryParse(request.Currency, out Currency currency);
            purchase.Currency = currencyIsValid ? currency : throw new InvalidCommandException($"{request.Currency} no es valido");
        }

        private static void UpdateCostIfNeeded(ModifyPurchaseCommand request, Purchase purchase)
        {
            purchase.Cost = request.Cost ?? purchase.Cost;
        }
    }

    public class ModifyPurchaseCommand : IRequest<PurchaseModel>
    {
        public int PurchaseId { get; set; }
        
        public string Name { get; set; }

        public ICollection<int> Participants { get; set; }

        public double? Cost { get; set; }

        public string Currency { get; set; }
    }
}