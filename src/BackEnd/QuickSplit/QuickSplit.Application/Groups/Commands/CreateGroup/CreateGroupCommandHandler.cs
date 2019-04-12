using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Interfaces;
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
            Group toCreate = new Group()
            {
                Id = request.Id,
                Name = request.Name,
                Admin = request.Admin
            };

            await _context.Groups.AddAsync(toCreate);
            await _context.SaveChangesAsync();

            return new GroupModel(toCreate);
        }
    }
}