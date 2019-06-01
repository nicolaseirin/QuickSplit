using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;
using QuickSplit.Application.Groups;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Groups.Queries;
using QuickSplit.Application.Purchases.Commands;
using QuickSplit.Application.Users.Models;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GroupsController : BaseController
    {
        [HttpPost]
        public async Task<ActionResult<GroupModel>> CreateGroup([FromBody] CreateGroupCommand command)
        {
            GroupModel newGroup = await Mediator.Send(command);
            return Ok(newGroup);
        }

        [HttpPut("leave")]
        public async Task<ActionResult<GroupModel>> LeaveGroup([FromBody] LeaveGroupCommand command)
        {
            await Mediator.Send(command);
            return Ok();
        }


        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await Mediator.Send(new DeleteGroupCommand
            {
                Id = id
            });

            return Ok();
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<GroupModel>> Put(int id, [FromBody] UpdateGroupCommand command)
        {
            command.Id = id;
            GroupModel updated = await Mediator.Send(command);
            return Ok(updated);
        }

        [HttpGet("{id}/memberships")]
        public async Task<ActionResult<ICollection<GroupModel>>> GetAll(int id)
        {
            IEnumerable<GroupModel> groups = await Mediator.Send(new GetGroupsQuery()
            {
                Id = id
            });

            return Ok(groups);

        }

        //[Authorize]
        [HttpGet("{id}")]
        public async Task<ActionResult<GroupModel>> Get(int id)
        {
            GroupModel group = await Mediator.Send(new GetGroupByIdQuery()
            {
                Id = id
            });
            return Ok(group);
        }

        [HttpPost("{id}/purchases")]
        public async Task<ActionResult<PurchaseModel>> AddPurchase([FromBody] CreatePurchaseCommand command)
        {
            PurchaseModel purchase = await Mediator.Send(command);
            return Ok(purchase);
        }

        [HttpGet("{id}/purchases")]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> GetPurchases(int id)
        {
            IEnumerable<PurchaseModel> result = await Mediator.Send(new GetPurchasesByGroupQuery()
            {
                GroupId = id
            });

            return Ok(result);
        }

        [HttpGet("{id}/users")]
        public async Task<ActionResult<IEnumerable<UserModel>>> GetParticipants(int id)
        {
            IEnumerable<UserModel> participants = await Mediator.Send(new GetMembersQuery()
            {
                GroupId = id
            });

            return Ok(participants);
        }
    }
}