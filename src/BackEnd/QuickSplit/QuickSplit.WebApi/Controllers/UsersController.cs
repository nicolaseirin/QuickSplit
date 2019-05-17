using System;
using System.Collections;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Commands.UpdateUser;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries.GetUserById;
using QuickSplit.Application.Users.Queries.GetUsers;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : BaseController
    {
    
        [Authorize]
        [HttpGet(Name = "GetUser")]
        public async Task<ActionResult<IEnumerable<UserModel>>> Get()
        {
            IEnumerable<UserModel> users = await Mediator.Send(new GetUsersQuery());
            return Ok(users);
        }

        [Authorize]
        [HttpGet("{id}")]
        public async Task<ActionResult<UserModel>> Get(int id)
        {
            UserModel user = await Mediator.Send(new GetUserByIdQuery()
            {
                Id = id
            });
            return Ok(user);
        }

        //[Authorize]
        [HttpPost]
        public async Task<IActionResult> Post([FromBody] CreateUserCommand user)
        {
            UserModel created =  await Mediator.Send(user);
            return CreatedAtRoute("GetUser", created);
        }

        [Authorize]
        [HttpPut("{id}")]
        public async Task<ActionResult<UserModel>> Put(int id, [FromBody] UpdateUserCommand command)
        {
            command.Id = id;
            UserModel updated = await Mediator.Send(command);
            return Ok(updated);
        }

        [Authorize]
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}