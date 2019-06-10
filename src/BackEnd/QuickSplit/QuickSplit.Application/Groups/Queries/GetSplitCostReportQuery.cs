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
using QuickSplit.Application.Purchases.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Domain;

namespace QuickSplit.Application.Groups.Queries
{
    public class GetSplitCostReportHandler : IRequestHandler<GetSplitCostReportQuery, IEnumerable<DebtorDebteeModel>>
    {
        private readonly IQuickSplitContext _context;
        private readonly IImageRepository _imageRepository;

        public GetSplitCostReportHandler(IQuickSplitContext context, IImageRepository imageRepository)
        {
            _context = context;
            _imageRepository = imageRepository;
        }

        public async Task<IEnumerable<DebtorDebteeModel>> Handle(GetSplitCostReportQuery request, CancellationToken cancellationToken)
        {
            Group @group = await _context.Groups.Include(group1 => group1.Memberships).Include(group1 => group1.Purchases).ThenInclude(purchase => purchase.Participants).FirstOrDefaultAsync(g => g.Id == request.GroupId, cancellationToken: cancellationToken) ?? throw new InvalidQueryException("No existe el grupo");
            Currency currency = GetCurrencyIfValid(request.Currency);
            
            return await Task.WhenAll(
                group
                    .GenerateSplitCostReport(currency)
                    .Dictionary
                    .Select(MapDebtorDebtee)
            );
        }

        private static Currency GetCurrencyIfValid(string c)
        {
            bool currencyIsValid = Enum.TryParse(c, out Currency currency);
            if (!currencyIsValid)
                throw new InvalidCommandException($"{c} no es una moneda valida");
            return currency;
        }
        
        private async Task<DebtorDebteeModel> MapDebtorDebtee(KeyValuePair<(User, User), double> pair)
        {
            Task<UserModel> debtor = MapUser(pair.Key.Item1);
            Task<UserModel> debtee = MapUser(pair.Key.Item2);

            return new DebtorDebteeModel()
            {
                Amount = pair.Value,
                Debtor = await debtor,
                Debtee = await debtee
            };
        }

        private async Task<UserModel> MapUser(User user)
        {
            string image = await _imageRepository.GetImageBase64(user.Id);

            return new UserModel(user, image);
        }
    }

    public class GetSplitCostReportQuery : IRequest<IEnumerable<DebtorDebteeModel>>
    {
        public int GroupId { get; set; }

        public string Currency { get; set; } = "Usd";
    }
}