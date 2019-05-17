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

namespace QuickSplit.Application.Groups.Commands.CreateGroup
{
    public class CreateGroupCommandHandler : IRequestHandler<CreateGroupCommand, GroupModel>
    {
        private readonly IQuickSplitContext _context;

        public CreateGroupCommandHandler(IQuickSplitContext context)
        {
            _context = context;
        }

        public async Task<GroupModel> Handle(CreateGroupCommand request, CancellationToken cancellationToken)
        {
            GroupModel response = null;
            try
            {
                response = await TryToHandle(request);
            }
            catch (DomainException ex)
            {
                throw new InvalidCommandException(ex.Message);
            }

            return response;
        }

        private async Task<GroupModel> TryToHandle(CreateGroupCommand request)
        {
            User admin = await _context.Users.FindAsync(request.Admin) ?? throw new InvalidCommandException($"El usuario administrador con id {request.Admin} no existe");
            
            var toCreate = new Group()
            {
                Name = request.Name,
                Admin = await _context.Users.FindAsync(request.Admin),
            };
            await _context.Groups.AddAsync(toCreate);
            await _context.SaveChangesAsync();

            Domain.Membership[] memberships = await Task.WhenAll(request.Memberships.Select(i => GetMemberships(i, toCreate)));
            toCreate.Memberships = memberships;
            await _context.SaveChangesAsync();

            return new GroupModel(toCreate);
        }

        private async Task<Domain.Membership> GetMemberships(int userId, Group group)
        {
            User user = await _context.Users.FindAsync(userId) ?? throw new InvalidCommandException($"Miembro del grupo con id {userId} no existe");;

            return new Domain.Membership()
            {
                User = user,
                UserId = user.Id,
                Group = group,
                GroupId = group.Id
            };
        }
    }
}