using System;
using System.Linq;
using System.Net.Http;
using MediatR;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using QuickSplit.Application.Interfaces;
using QuickSplit.Domain;
using QuickSplit.Persistence;
using QuickSplit.WebApi;
using QuickSplit.WebApi.Filters;

namespace QuickSplit.Tests.Integration
{
    public class CustomWebApplicationFactory : WebApplicationFactory<Startup>
    {
        // Pone aca todas las tablas de la base
        private readonly string[] _tables = new[] {"Users"};

        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            builder.ConfigureServices(services =>
            {
                // Create a new service provider.
                ServiceProvider serviceProvider = new ServiceCollection()
                    .AddEntityFrameworkSqlServer()
                    .AddTransient<IQuickSplitContext, QuickSplitContext>()
                    .AddMediatR(typeof(IQuickSplitContext).Assembly)
                    .BuildServiceProvider();

                // Add a database context (ApplicationDbContext) using an in-memory 
                // database for testing.
                services.AddDbContext<QuickSplitContext>(options =>
                {
                    options.UseSqlServer("Server=localhost;Database=QuickSplitDbTesting;Trusted_Connection=False;User ID=QuickSplit;Password=QuickSplit123;");
                    options.UseInternalServiceProvider(serviceProvider);
                });

                services.AddMvc(AddFilters);

                // Build the service provider.
                ServiceProvider sp = services.BuildServiceProvider();

                // Create a scope to obtain a reference to the database
                // context (ApplicationDbContext).
                using (IServiceScope scope = sp.CreateScope())
                {
                    IServiceProvider scopedServices = scope.ServiceProvider;
                    var db = scopedServices.GetRequiredService<QuickSplitContext>();

                    // Resets Database
                    db.Database.EnsureCreated();
                    foreach (string table in _tables)
                    {
                        db.Database.ExecuteSqlCommand(@"TRUNCATE TABLE dbo." + table);
                        db.Database.ExecuteSqlCommand(@"INSERT INTO dbo." +  table + " (Name, LastName, Mail, Password) VALUES ('admin', 'admin', 'admin@gmail.com','DVOZUIQnznlVbNpxkYAgwejRW1M=')");
                    }

                    //db.SaveChanges();
                    //var users = db.Users.ToList();
                    //db.RemoveRange(users);
//                    db.Users.Add(new User()
//                    {
//                        Id = 1,
//                        Name = "Admin",
//                        LastName = "Admin",
//                        Mail = "admin@gmail.com",
//                        Password = "123"
//                    });
//                    db.SaveChanges();
                }
            });
        }

        private void AddFilters(MvcOptions options)
        {
            options.Filters.Add(new ExceptionFilter());
        }
    }
}