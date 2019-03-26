using System.Threading;
using System.Threading.Tasks;
using MediatR;

namespace QuickSplit.Application.Values.Commands.CreateValue
{
    public class CreateValueCommandHandler : IRequestHandler<CreateValueCommand, Unit>
    {
        
        
        public async Task<Unit> Handle(CreateValueCommand request, CancellationToken cancellationToken)
        {
            string valueToCreate = request.Value; // Los valores que venian adrento del command

            await CreateValueInDataBase();

            return Unit.Value; // Unit es como un void para task nose porque pero ta es asi 
        }

        private async Task CreateValueInDataBase() //Simula llamar al Entity Framework o al IRepo
        {
            await Task.Delay(100);
        }
        
        
    }
}