using System;
using MediatR;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using QuickSplit.Application.Interfaces;
using QuickSplit.Persistence;
using QuickSplit.WebApi;
using QuickSplit.WebApi.Filters;

namespace QuickSplit.Tests.Integration.Internal
{
    public class CustomWebApplicationFactory : WebApplicationFactory<Startup>
    {
        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            builder.ConfigureServices(services =>
            {
                // Create a new service provider.
                ServiceProvider serviceProvider = new ServiceCollection()
                    .AddEntityFrameworkSqlServer()
                    .AddEntityFrameworkProxies()
                    .AddTransient<IQuickSplitContext, QuickSplitContext>()
                    .AddMediatR(typeof(IQuickSplitContext).Assembly)
                    .BuildServiceProvider();

                // Add a database context (ApplicationDbContext) using an in-memory 
                // database for testing.
                services.AddDbContext<QuickSplitContext>(options =>
                {
                    //options.UseLazyLoadingProxies()
                    options.UseSqlServer("Server=localhost;Database=QuickSplitDbTesting;Trusted_Connection=False;User ID=QuickSplit;Password=QuickSplit123;");
                    //options.UseSqlServer("Server=DESKTOP-DN1E4B9\\SQLSERVER_R14;Database=QuickSplitDbTest;Trusted_Connection=True;");
                    options.UseInternalServiceProvider(serviceProvider);
                });

                services.AddMvc(AddFilters);

                // Build the service provider.
                ServiceProvider sp = services.BuildServiceProvider();


                // Reseteo la base de datos y le agrego el admin
                using (IServiceScope scope = sp.CreateScope())
                {
                    IServiceProvider scopedServices = scope.ServiceProvider;
                    var db = scopedServices.GetRequiredService<QuickSplitContext>();

                    // Resets Database
                    db.Database.EnsureDeleted();
                    db.Database.EnsureCreated();
                    db.Database.ExecuteSqlCommand(@"INSERT INTO dbo.Users (Name, LastName, Mail, Password) VALUES ('admin', 'admin', 'admin@gmail.com','DVOZUIQnznlVbNpxkYAgwejRW1M=')");
                }
            });
        }

        private void AddFilters(MvcOptions options)
        {
            options.Filters.Add(new ExceptionFilter());
        }
        
        private const string WipeDbScript = @"
            SET QUOTED_IDENTIFIER ON;
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON; ALTER TABLE ? NOCHECK CONSTRAINT ALL'
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON; ALTER TABLE ? DISABLE TRIGGER ALL'
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON; DELETE FROM ?'
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON; ALTER TABLE ? CHECK CONSTRAINT ALL'
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON; ALTER TABLE ? ENABLE TRIGGER ALL'
            EXEC sp_MSforeachtable 'SET QUOTED_IDENTIFIER ON';

            IF NOT EXISTS (
                    SELECT
                        *
                    FROM
                        SYS.IDENTITY_COLUMNS
                            JOIN SYS.TABLES ON SYS.IDENTITY_COLUMNS.Object_ID = SYS.TABLES.Object_ID
                    WHERE
                            SYS.TABLES.Object_ID = OBJECT_ID('?') AND SYS.IDENTITY_COLUMNS.Last_Value IS NULL
                )
                AND OBJECTPROPERTY( OBJECT_ID('?'), 'TableHasIdentity' ) = 1

                DBCC CHECKIDENT ('?', RESEED, 0) WITH NO_INFOMSGS;";
    }
}