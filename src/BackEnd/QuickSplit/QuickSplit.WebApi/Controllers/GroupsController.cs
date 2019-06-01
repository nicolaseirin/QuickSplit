﻿using System;
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

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GroupsController : BaseController
    {
        [Authorize]
        [HttpGet("{id}")]
        public async Task<ActionResult<GroupModel>> Get(int id)
        {
            GroupModel group = await Mediator.Send(new GetGroupByIdQuery()
            {
                Id = id
            });
            return Ok(group);
        }

        [Authorize]
        [HttpPost]
        public async Task<ActionResult<GroupModel>> CreateGroup([FromBody] CreateGroupCommand command)
        {
            GroupModel newGroup = await Mediator.Send(command);
            return Ok(newGroup);
        }

        [Authorize]
        [HttpPut("{id}")]
        public async Task<ActionResult<GroupModel>> Put(int id, [FromBody] UpdateGroupCommand command)
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


        [HttpGet("{id}/memberships")]
        public async Task<ActionResult<ICollection<GroupModel>>> GetAll(int id)
        {
            IEnumerable<GroupModel> groups = await Mediator.Send(new GetGroupsQuery()
            {
                Id = id
            });

            return Ok(groups);
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
    }
}