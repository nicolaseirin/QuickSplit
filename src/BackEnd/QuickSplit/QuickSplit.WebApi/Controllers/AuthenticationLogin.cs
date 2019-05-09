using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using QuickSplit.Application.Users.Queries.GetPassword;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthenticationsController : BaseController
    {

        // POST api/values
        [HttpPost]
        public async Task<ActionResult<string>> Post([FromBody] PasswordIsValidQuery query)
        {
            bool validLogin =  await Mediator.Send(query);

            if (validLogin)
                return Ok(CreateToken(query.Mail, query.Password));
            else
                return BadRequest("Combinacion de usuario y contraseña invalida.");
        }

        public object CreateToken(string mail, string password)
        {
            var secretKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(Configuration["Secret"]));
            var signinCredentials = new SigningCredentials(secretKey, SecurityAlgorithms.HmacSha256);
            var tokenOptions = new JwtSecurityToken(
                //issuer: "http://localhost:5000",
                //audience: "http://localhost:5000",
                claims: new List<Claim>{
                    new Claim("Id", mail),
                    new Claim("Password", password),
                },
                expires: DateTime.Now.AddDays(1),
                signingCredentials: signinCredentials
            );

            return new
            {
                Token = new JwtSecurityTokenHandler().WriteToken(tokenOptions)
            };
        }
    }
}