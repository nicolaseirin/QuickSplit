using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Purchases.Commands;
using QuickSplit.Application.Purchases.Queries;
using QuickSplit.Application.Users.Queries;

namespace QuickSplit.WebApi.Controllers
{
    [Route("api/[controller]")]
    [Controller]
    public class PurchasesController : BaseController
    {
        [HttpGet]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> GetPurchases()
        {
            IEnumerable<PurchaseModel> purchases = await Mediator.Send(new GetPurchasesQuery());

            return Ok(purchases);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> GetPurchases(int id)
        {
            PurchaseModel purchase = await Mediator.Send(new GetPurchaseByIdQuery()
            {
                Id = id
            });

            return Ok(purchase);
        }
        
        [HttpGet("{id}/image")]
        public async Task<ActionResult<Stream>> GetPurchaseImage(int id)
        {
            Stream image = await Mediator.Send(new GetPurchaseImageQuery()
            {
                PurchaseId = id
            });

            return Ok(image);
        }

        [HttpPost]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> CreatePurchase([FromBody] CreatePurchaseCommand command)
        {
            PurchaseModel purchase = await Mediator.Send(command);

            return Ok(purchase);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<IEnumerable<PurchaseModel>>> ModifyPurchase(int id, [FromBody] ModifyPurchaseCommand command)
        {
            command.PurchaseId = id;
            PurchaseModel purchase = await Mediator.Send(command);

            return Ok(purchase);
        }
    }
}