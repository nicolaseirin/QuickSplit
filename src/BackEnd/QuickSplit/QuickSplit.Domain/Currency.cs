using System;

namespace QuickSplit.Domain
{
    public enum Currency
    {
        Usd, Uyu, Ars
    }
    
    public static class CurrencyUtil {

        public static double ToUsd(this Currency currency, double value)
        {
            switch (currency)
            {
                case Currency.Usd:
                    return value;
                case Currency.Uyu:
                    return value / 30;
                case Currency.Ars:
                    return value / 45;
                default:
                    throw new ArgumentOutOfRangeException(nameof(currency), currency, null);
            }
        }

        public static double FromUsd(this Currency currency, double value)
        {
            switch (currency)
            {
                case Currency.Usd:
                    return value;
                case Currency.Uyu:
                    return value * 30;
                case Currency.Ars:
                    return value * 45;
                default:
                    throw new ArgumentOutOfRangeException(nameof(currency), currency, null);
            }
        }
    }
}