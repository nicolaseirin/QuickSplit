using System;
using System.IO;
using System.Net.NetworkInformation;
using MediatR;
using MediatR.Pipeline;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using QuickSplit.Application.Interfaces;
using QuickSplit.Persistence;

namespace QuickSplit.Test.Application
{
    public static class TestingFactory
    {
        public static IQuickSplitContext CreateInMemoryContext()
        {
            Guid id = Guid.NewGuid();
            var options = new DbContextOptionsBuilder<QuickSplitContext>()
                .UseInMemoryDatabase(id.ToString())
                .EnableSensitiveDataLogging()
                .Options;
            return new QuickSplitContext(options);
        }
    }
}