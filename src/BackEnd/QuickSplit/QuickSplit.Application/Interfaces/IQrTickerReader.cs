using System.IO;
using System.Threading.Tasks;
using QuickSplit.Domain;

namespace QuickSplit.Application.Interfaces
{
    public interface IQrTickerReader
    {
        Task<(double, Currency)> ReadTicket(string url);
    }
}