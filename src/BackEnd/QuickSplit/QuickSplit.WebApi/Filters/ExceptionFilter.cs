using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using QuickSplit.Application.Exceptions;

namespace QuickSplit.WebApi.Filters
{
    public class ExceptionFilter : ExceptionFilterAttribute
    {
        public override Task OnExceptionAsync(ExceptionContext context)
        {
            switch (context.Exception)
            {
                case InvalidCommandException ex:
                    context.Result = new BadRequestObjectResult(ex.Message);
                    break;
                case InvalidQueryException ex:
                    context.Result = new NotFoundObjectResult(ex.Message);
                    break;
            }

            return Task.CompletedTask;
        }

        public override void OnException(ExceptionContext context)
        {
            base.OnException(context);
        }
    }
}