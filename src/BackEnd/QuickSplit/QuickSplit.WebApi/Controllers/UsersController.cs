using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Users.Commands.CreateUser;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries.GetUsers;
using QuickSplit.Application.Values.Commands.CreateValue;
using QuickSplit.Application.Values.Queries.GetValues;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : CQRSController
    {
        // GET api/values
        [HttpGet(Name = "GetUser")]
        public async Task<ActionResult<IEnumerable<UserModel>>> Get()
        {
            return Ok(await Mediator.Send(new GetUsersQuery()));
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
            await Mediator.Send(user);
            return CreatedAtRoute("GetUser", user);
        }

        // PUT api/values/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/values/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}