using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using QuickSplit.Application.Groups.Models;
using Remotion.Linq.Parsing.Structure.IntermediateModel;
using QuickSplit.Application.Groups.Commands.CreateGroup;
using QuickSplit.Application.Groups.Commands.DeleteGroup;
using QuickSplit.Application.Groups.Commands.UpdateGroup;

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
    }
}
