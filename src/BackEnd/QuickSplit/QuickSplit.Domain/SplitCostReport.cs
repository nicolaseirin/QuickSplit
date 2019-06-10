using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Cache;

namespace QuickSplit.Domain
{
    public class SplitCostReport
    {
        private readonly Dictionary<(User, User), double> _dictionary = new Dictionary<(User, User), double>();
        
        public SplitCostReport(Group @group, Currency currency)
        {
            foreach (Purchase purchase in group.Purchases.Select(p => ConvertToCurrency(p, currency)))
            {
                int participants = purchase.Participants.Count + 1; // Participants plus purchaser
                double portion = purchase.Cost / participants;
                
                foreach (Participant participant in purchase.Participants)
                {
                    AddDebtToReport(participant.User, purchase.Purchaser, portion);
                }
            }

            foreach ((User, User) pair in _dictionary.Keys.ToList())
            {
                double value = _dictionary[pair];
                (User, User) inversePair = (pair.Item2, pair.Item1);

                if (!_dictionary.ContainsKey(inversePair)) continue;
                
                double inverseVal = _dictionary[inversePair];
                double difference = Math.Abs(value - inverseVal);

                if (value < inverseVal)
                {
                    _dictionary[pair] = 0;
                    _dictionary[inversePair] = difference;
                }
                else
                {
                    _dictionary[pair] = difference;
                    _dictionary[inversePair] = 0;
                }
            }

            foreach (var pair in _dictionary.Where(pair => Math.Abs(pair.Value) < 0.001).ToList())
            {
                _dictionary.Remove(pair.Key);
            }
        }

        private void AddDebtToReport(User debtor, User debtee, double portion)
        {
            (User, User) debtorDebtee = (debtor, debtee);
            if (!_dictionary.ContainsKey(debtorDebtee))
            {
                _dictionary[debtorDebtee] = 0d;
            }

            _dictionary[debtorDebtee] += portion;
        }

        public double this[(User, User) key] => _dictionary[key];

        public IReadOnlyDictionary<(User, User), double> Dictionary => _dictionary;

        private Purchase ConvertToCurrency(Purchase purchase, Currency currency)
        {
            double cost = purchase.Currency.ToUsd(purchase.Cost);
            cost = currency.FromUsd(cost);
            
            return new Purchase()
            {
                Name = purchase.Name,
                Id = purchase.Id,
                Participants =  purchase.Participants,
                Purchaser = purchase.Purchaser,
                Group = purchase.Group,
                Currency = currency,
                Cost = cost
            };
        }
    }
}