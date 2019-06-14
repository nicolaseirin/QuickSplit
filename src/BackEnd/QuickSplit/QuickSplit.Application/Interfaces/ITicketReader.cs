using System.IO;
using QuickSplit.Domain;

namespace QuickSplit.Application.Interfaces
{
    public interface ITicketReader
    {
        (Currency, double) ReadTicket(Stream requestQrImage);
    }
}