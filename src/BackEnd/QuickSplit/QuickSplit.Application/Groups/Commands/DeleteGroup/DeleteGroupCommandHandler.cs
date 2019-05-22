using System.Data;
using System.Threading;
using System.Threading.Tasks;
using System.Linq;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;


namespace QuickSplit.Application.Groups.Commands.DeleteGroup
{
    public class DeleteGroupCommandHandler: IRequestHandler<DeleteGroupCommand>
    {
        private readonly IQuickSplitContext _context;

        public DeleteGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(DeleteGroupCommand request, CancellationToken cancellationToken)
        {
            Group toDelete = await _context.Groups.FindAsync(request.Id);
            if (toDelete == null)
                throw new InvalidCommandException($"No existe el grupo con id {request.Id}");

            DeleteMemberships(request.Id);
            _context.Groups.Remove(toDelete);
            await _context.SaveChangesAsync();

            return Unit.Value;
        }

        private async void DeleteMemberships(int groupId)
        {
            var memberships = _context.Memberships.Where(m=> m.GroupId == groupId).ToList();
            foreach (var mem in memberships)
            {
                _context.Memberships.Remove(mem);
               _context.SaveChanges();
            }
        }
    }
}