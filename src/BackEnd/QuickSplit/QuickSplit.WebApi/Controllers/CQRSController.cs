using MediatR;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.DependencyInjection;

namespace QuickSplit.WebApi.Controllers
{
    public class CQRSController : ControllerBase
    {
        private IMediator mediator;
        
        protected IMediator Mediator => mediator ?? (mediator = HttpContext.RequestServices.GetService<IMediator>());

    }
}