using System.Collections.Generic;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Configuration.EnvironmentVariables;
using Microsoft.Extensions.Configuration.Memory;
using QuickSplit.WebApi;

namespace QuickSplit.Tests.Integration
{
    public class TestStartup : Startup
    {
        public TestStartup() : base(new ConfigurationRoot(new List<IConfigurationProvider>()
        {
            new MemoryConfigurationProvider(new MemoryConfigurationSource()
            {
                InitialData = new[]
                {
                    new KeyValuePair<string, string>("QuickSplitDb", "Server=localhost;Database=QuickSplitDb;Trusted_Connection=False;User ID=QuickSplit;Password=QuickSplit123;"),
                }
            })
        }))
        {
        }
    }
}