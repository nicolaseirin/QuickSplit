using System;
using System.Net.Http;
using Microsoft.AspNetCore.Mvc.Testing;
using Xunit;

namespace QuickSplit.Tests.IntegrationTests
{
    public class UnitTest1 : IClassFixture<QuickSplit.WebApi.Startup>
    {
        private readonly HttpClient _client;

        public UnitTest1(WebApplicationFactory<QuickSplit.WebApi.Startup> factory)
        {
            _client = factory.CreateClient(new WebApplicationFactoryClientOptions());
        }

        [Fact]
        public void Test1()
        {
            Assert.True(true);
        }
    }
}