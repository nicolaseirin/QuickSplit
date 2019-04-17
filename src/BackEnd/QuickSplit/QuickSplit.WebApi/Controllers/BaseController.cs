using MediatR;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

namespace QuickSplit.WebApi.Controllers
{
    public class BaseController : ControllerBase
    {
        private IMediator _mediator;
        private IConfiguration _configuration;
        
        protected IMediator Mediator => _mediator ?? (_mediator = HttpContext.RequestServices.GetService<IMediator>());
        protected IConfiguration Configuration => _configuration ?? (_configuration = HttpContext.RequestServices.GetService<IConfiguration>());

    }
}