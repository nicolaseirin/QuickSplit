using MediatR;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.DependencyInjection;

namespace QuickSplit.WebApi.Controllers
{
    public class BaseController : ControllerBase
    {
        private IMediator mediator;
        
        protected IMediator Mediator => mediator ?? (mediator = HttpContext.RequestServices.GetService<IMediator>());

    }
}