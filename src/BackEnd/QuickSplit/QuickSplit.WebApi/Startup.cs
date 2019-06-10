using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MediatR;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.HttpsPolicy;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Diagnostics;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;
using QuickSplit.Persistence;
using QuickSplit.WebApi.Filters;

namespace QuickSplit.WebApi
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
           SetupAuthentication(services);
            
            services.AddTransient<IQuickSplitContext, QuickSplitContext>();
            services.AddTransient<IPasswordHasher, PasswordHasher.PasswordHasher>();
            services.AddTransient<IImageRepository, ImageRepository>();
            services.AddDbContext<QuickSplitContext>(GenerateDbOptions);

            
            
            //Add MediatR Commands and Queries
            services.AddMediatR(typeof(IQuickSplitContext).Assembly);

            services
                .AddMvc(AddFilters)
                .SetCompatibilityVersion(CompatibilityVersion.Version_2_2);
        }

        private void AddFilters(MvcOptions options)
        {
            options.Filters.Add(new ExceptionFilter());
        }
        
        private void SetupAuthentication(IServiceCollection services)
        {
            services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options =>
                {
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateIssuer = false,
                        ValidateAudience = false,
                        ValidateLifetime = true,
                        ValidateIssuerSigningKey = true,

                        //ValidIssuer = "http://localhost:5000",
                        //ValidAudience = "http://localhost:5000",
                        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(Configuration["Secret"]))
                    };
                });
        }

        private void GenerateDbOptions(IServiceProvider serviceProvider, DbContextOptionsBuilder options)
        {
            options.UseLazyLoadingProxies();
            options.UseSqlServer(Configuration.GetConnectionString("QuickSplitDb"));
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }

            app.UseAuthentication();
            app.UseMvc();
        }


    }
}
