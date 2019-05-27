
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Membership.Queries.GetMemberships;
using QuickSplit.Domain;


namespace QuickSplit.Application.Groups.Commands
{
    public class LeaveGroupCommandHandler: IRequestHandler<LeaveGroupCommand>
    {
        private readonly IQuickSplitContext _context;

        public LeaveGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<Unit> Handle(LeaveGroupCommand request, CancellationToken cancellationToken)
        {
            try
            {
                await TryToHandle(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }

            return Unit.Value;            
        }

        private async Task<Unit> TryToHandle(LeaveGroupCommand request)
        {
            User user = await _context.Users.FindAsync(request.UserId) ?? throw new InvalidCommandException($"El usuario con id {request.UserId} no existe");
            Group group = await _context.Groups.FindAsync(request.GroupId) ?? throw new InvalidCommandException($"El grupo con id {request.GroupId} no existe");
            Domain.Membership membership = await _context.Memberships.FindAsync(request.GroupId) ?? throw new InvalidCommandException($"La membresia grupo: {request.GroupId} - usuario: {request.UserId} no existe");
           
            group.Memberships.Remove(membership);

           // await _context.Memberships.Remove(membership);
            
            await _context.SaveChangesAsync();


           return Unit.Value;
        }
    }
}


