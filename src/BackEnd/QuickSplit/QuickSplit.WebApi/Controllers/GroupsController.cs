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
using Microsoft.AspNetCore.Authorization;
using QuickSplit.Application.Users.Queries;
using GetGroupsQuery = QuickSplit.Application.Groups.Queries.GetGroupsQuery;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GroupsController : BaseController
    {
        [HttpGet("{id}")]
        public async Task<ActionResult<GroupModel>> Get(int id)
        {
            GroupModel group = await Mediator.Send(new GetGroupByIdQuery()
            {
                Id = id
            });
            return Ok(group);
        }

        [HttpGet]
        public async Task<ActionResult<GroupModel>> GetAll()
        {
            IEnumerable<GroupModel> group = await Mediator.Send(new GetGroupsQuery());
            return Ok(group);
        }


        [HttpPost]
        public async Task<ActionResult<GroupModel>> CreateGroup([FromBody] CreateGroupCommand command)
        {
            GroupModel newGroup = await Mediator.Send(command);
            return Ok(newGroup);
        }

        [Authorize]
        [HttpPut("{id}")]
        public async Task<ActionResult<GroupModel>> Put(int id, [FromBody] ModifyGroupCommand command)
        {
            command.Id = id;
            GroupModel updated = await Mediator.Send(command);
            return Ok(updated);
        }

        [Authorize]
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            await Mediator.Send(new DeleteGroupCommand
            {
                Id = id
            });

            return Ok();
        }

        [Authorize]
        [HttpPut("leave")]
        public async Task<IActionResult> LeaveGroup([FromBody] LeaveGroupCommand command)
        {
            await Mediator.Send(command);
            return Ok();
        }


        [Authorize]
        [HttpGet("{id}/users")]
        public async Task<ActionResult<IEnumerable<UserModel>>> GetParticipants(int id)
        {
            IEnumerable<UserModel> participants = await Mediator.Send(new GetMembersQuery()
            {
                GroupId = id
            });

            return Ok(participants);
        }

        [Authorize]
        [HttpGet("{id}/purchases")]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> GetPurchases(int id)
        {
            IEnumerable<PurchaseModel> result = await Mediator.Send(new GetPurchasesByGroupQuery()
            {
                GroupId = id
            });

            return Ok(result);
        }

        [Authorize]
        [HttpPost("{id}/purchases")]
        public async Task<ActionResult<PurchaseModel>> AddPurchase([FromBody] CreatePurchaseCommand command)
        {
            PurchaseModel purchase = await Mediator.Send(command);
            return Ok(purchase);
        }

        [HttpGet("{id}/reports")]
        public async Task<ActionResult<IEnumerable<DebtorDebteeModel>>> GetSplitReport(int id, [FromQuery] string currency)
        {
            IEnumerable<DebtorDebteeModel> debts = await Mediator.Send(new GetSplitCostReportQuery()
            {
                GroupId = id,
                Currency = currency ?? "Usd"
            });
            return Ok(debts);
        }
    }
}