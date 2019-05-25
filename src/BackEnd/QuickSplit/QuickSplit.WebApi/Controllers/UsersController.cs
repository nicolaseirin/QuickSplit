using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Mime;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Users.Commands;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Users.Queries;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : BaseController
    {
        [Authorize]
        [HttpGet(Name = "GetUser")]
        public async Task<ActionResult<IEnumerable<UserModel>>> Get([FromQuery] string find, [FromQuery] int? excludeFriendsOfId)
        {
            IEnumerable<UserModel> users = await Mediator.Send(new GetUsersQuery()
            {
                SearchNameQuery = find,
                NotFriendWithQuery = excludeFriendsOfId
            });
            return Ok(users);
        }

        [Authorize]
        [HttpGet("{id}")]
        public async Task<ActionResult<UserModel>> Get(int id)
        {
            UserModel user = await Mediator.Send(new GetUserByIdQuery
            {
                Id = id
            });
            return Ok(user);
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromBody] CreateUserCommand user)
        {
            UserModel created = await Mediator.Send(user);
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
        public async Task<IActionResult> Delete(int id)
        {
            await Mediator.Send(new DeleteUserCommand
            {
                Id = id
            });

            return Ok();
        }

        [Authorize]
        [HttpGet("{id}/friends")]
        public async Task<IActionResult> GetFriends(int id)
        {
            IEnumerable<UserModel> friends = await Mediator.Send(new GetFriendsQuery() {UserId = id});
            return Ok(friends);
        }

        [Authorize]
        [HttpPost("{id}/friends/{friendId}")]
        public async Task<IActionResult> AddFriend(int id, int friendId)
        {   
            await Mediator.Send(new AddFriendCommand(){
                CurrentUserId = id,
                FriendUserId = friendId
            });
            return Ok();
        }

        [Authorize]
        [HttpDelete("{id}/friends/{friendId}")]
        public async Task<IActionResult> DeleteFriend(int id, int friendId)
        {
            await Mediator.Send(new DeleteFriendCommand()
            {
                CurrentUserId = id,
                FriendUserId = friendId
            });
            return Ok();
        }

        [Authorize]
        [HttpGet("{id}/avatars")]
        public async Task<ActionResult> GetImage(int id)
        {
            var command = new GetAvatarQuery()
            {
                UserId = id,
            };
            Stream stream = await Mediator.Send(command);

            return Ok(stream);
        }
        
        [Authorize]
        [HttpPost("{id}/avatars")]
        [Consumes("image/jpg", "image/jpeg", "image/png", "multipart/form-data")]
        public async Task<IActionResult> AddImage(int id, IFormFile image)
        {
            
            var command = new AddOrUpdateAvatarCommand()
            {
                UserId = id,
                ImageStream = image.OpenReadStream(),
                ImageFormat = image.ContentType.Split().Last()
            };
            
            await Mediator.Send(command);
            

            return Ok();
        }
    }
}