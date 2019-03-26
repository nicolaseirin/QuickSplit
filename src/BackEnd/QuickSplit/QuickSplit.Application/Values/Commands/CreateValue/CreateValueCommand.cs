using MediatR;

namespace QuickSplit.Application.Values.Commands.CreateValue
{
    public class CreateValueCommand : IRequest
    {
        public string Value { get; set; }
        
    }
}