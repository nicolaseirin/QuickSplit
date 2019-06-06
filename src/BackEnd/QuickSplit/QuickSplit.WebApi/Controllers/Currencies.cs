using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Purchases.Queries;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class Currencies : BaseController
    {
        // GET
        [HttpGet]
        public async Task<ActionResult<IEnumerable<string>>> GetCurrencies()
        {
            IEnumerable<string> currencies = await Mediator.Send(new GetCurrenciesQuery());
            return Ok(currencies);
        }
    }
}