using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using QuickSplit.Application.Groups.Models;
using Remotion.Linq.Parsing.Structure.IntermediateModel;
using QuickSplit.Application.Groups.Commands.CreateGroup;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class GroupController: BaseController
    {
        //POST
        [HttpPost]
        public async Task<ActionResult<GroupModel>> Put(int id, [FromBody] CreateGroupCommand command)
        {
            GroupModel newGroup = await Mediator.Send(command);
            return Ok(newGroup);
        }
    }
}
