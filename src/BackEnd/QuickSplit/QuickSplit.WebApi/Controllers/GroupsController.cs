using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using QuickSplit.Application.Groups.Commands;
using QuickSplit.Application.Groups.Models;
using Remotion.Linq.Parsing.Structure.IntermediateModel;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GroupsController: BaseController
    {
        //POST
        [HttpPost]
        public async Task<ActionResult<GroupModel>> CreateGroup([FromBody] CreateGroupCommand command)
        {
            GroupModel newGroup = await Mediator.Send(command);
            return Ok(newGroup);
        }

        [HttpPost("{id}/purchases")]
        public async Task<ActionResult<PurchaseModel>> AddPurchase([FromBody] AddPurchaseCommand command)
        {
            PurchaseModel purchase = await Mediator.Send(command);
            return Ok(purchase);
        }
    }
}
