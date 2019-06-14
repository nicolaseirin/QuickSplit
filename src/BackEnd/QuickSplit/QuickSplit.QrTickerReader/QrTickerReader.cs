using System.IO;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;

namespace QuickSplit.QrTickerReader
{
    public class QrTickerReader : IQrTickerReader
    {
        private const string CostSpanId = "id=\"span_vMONTO\"";
        
        public async Task<(double, Currency)> ReadTicket(string url)
        {
            var client = new HttpClient();
            var result = await client.GetStringAsync(url);

            var aux = result.IndexOf(CostSpanId);
            var start = result.Substring(aux).IndexOf('')
        }
    }
}