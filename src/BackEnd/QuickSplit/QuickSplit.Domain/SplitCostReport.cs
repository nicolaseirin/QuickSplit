using System;
using System.Collections.Generic;

namespace QuickSplit.Domain
{
    public class SplitCostReport
    {
        private readonly Dictionary<(User, User), double> _report = new Dictionary<(User, User), double>();
        public SplitCostReport(Group @group)
        {
            foreach (Purchase purchase in group.Purchases)
            {
                int participants = purchase.Participants.Count + 1; // Participants plus purchaser
                double portion = purchase.Cost / participants;
                
                foreach (Participant participant in purchase.Participants)
                {
                    AddDebtToReport(participant.User, purchase.Purchaser, portion);
                }
            }
        }

        private void AddDebtToReport(User debtor, User debtee, double portion)
        {
            (User, User) debtorDebtee = (debtee, debtee);
            if (_report.ContainsKey(debtorDebtee))
            {
                _report[debtorDebtee] = 0d;
            }

            _report[debtorDebtee] += portion;
        }


        public IReadOnlyDictionary<(User, User), double> Report => _report;
    }
}