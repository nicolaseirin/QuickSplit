using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Commands.UpdateUser;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries.GetUsers;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : BaseController
    {
        // GET api/values
        [HttpGet(Name = "GetUser")]
        public async Task<ActionResult<IEnumerable<UserModel>>> Get()
        {
            IEnumerable<UserModel> users = await Mediator.Send(new GetUsersQuery());
            return Ok(users);
        }

        // GET api/values/5
        [HttpGet("{id}")]
        public ActionResult<string> Get(int id)
        {
            return "value";
        }

        // POST api/values
        [HttpPost]
        public async Task<IActionResult> Post([FromBody] CreateUserCommand user)
        {
            UserModel created =  await Mediator.Send(user);
            return CreatedAtRoute("GetUser", created);
        }

        // PUT api/values/5
        [HttpPut("{id}")]
        public async Task<ActionResult<UserModel>> Put(int id, [FromBody] UpdateUserCommand command)
        {
            command.Id = id;
            UserModel updated = await Mediator.Send(command);
            return Ok(updated);
        }

        // DELETE api/values/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}