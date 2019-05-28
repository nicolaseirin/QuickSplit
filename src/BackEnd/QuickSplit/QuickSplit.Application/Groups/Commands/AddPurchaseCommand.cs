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

namespace QuickSplit.Application.Groups.Commands
{
    public class AddPurchaseCommandHandler : IRequestHandler<AddPurchaseCommand, PurchaseModel>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public AddPurchaseCommandHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
            _imageRepository.FolderName = "Purchases";
        }

        public async Task<PurchaseModel> Handle(AddPurchaseCommand request, CancellationToken cancellationToken)
        {
            Group group = await GetGroupIfValid(request);
            User purchaser = await GetPurchaserIfValid(request);
            IEnumerable<User> participants = await GetParticipantsIfValid(request, group);
            Currency currency = GetCurrencyIfValid(request);

            var purchase = new Purchase(purchaser, group, request.Cost, currency, participants);
            group.Purchases.Add(purchase);
            await _context.SaveChangesAsync();

            if (!string.IsNullOrWhiteSpace(request.Image) && !string.IsNullOrWhiteSpace(request.ImageExt)) 
                _imageRepository.AddImageFromBase64(purchase.Id, request.Image, request.ImageExt);
            
            return new PurchaseModel(purchase);
        }

        private static Currency GetCurrencyIfValid(AddPurchaseCommand request)
        {
            bool currencyIsValid = Enum.TryParse(request.Currency, out Currency currency);
            if (!currencyIsValid)
                throw new InvalidCommandException($"{request.Currency} no es una moneda valida");
            return currency;
        }

        private async Task<User[]> GetParticipantsIfValid(AddPurchaseCommand request, Group @group)
        {
            User[] participants = await Task.WhenAll(request.Participants.Select(u => _context.Users.FindAsync(u)));
            if (participants.Any(u => u == null)) 
                throw new InvalidCommandException("Participantes invalidos");

            return participants;
        }

        private async Task<User> GetPurchaserIfValid(AddPurchaseCommand request)
        {
            return await _context.Users.FindAsync(request.Purchaser) ?? throw new InvalidCommandException("El comprador no existe");
        }

        private async Task<Group> GetGroupIfValid(AddPurchaseCommand request)
        {
            return await _context.Groups.FindAsync(request.Group) ?? throw new InvalidCommandException("Grupo no existe");
        }
    }

    public class AddPurchaseCommand : IRequest<PurchaseModel>
    {
        public int Purchaser { get; set; }

        public int Group { get; set; }

        public IEnumerable<int> Participants { get; set; }

        public uint Cost { get; set; }

        public string Currency { get; set; }
        
        public string Image { get; set; }
        
        public string ImageExt { get; set; }
    }
}