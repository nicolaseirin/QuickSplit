using System.IO;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;
using Bytescout.BarCodeReader;

namespace QuickSplit.TickerReader
{
    public class TicketReader : ITicketReader
    {
        public (Currency, double) ReadTicket(Stream requestQrImage)
        {
            Bytescout.BarCodeReader.Reader reader = new Reader();
            reader.
            throw new System.NotImplementedException();
            
        }
    }
}