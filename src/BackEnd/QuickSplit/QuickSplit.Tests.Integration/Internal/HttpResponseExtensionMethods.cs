using System.Collections;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;

namespace QuickSplit.Tests.Integration
{
    public static class HttpResponseExtensionMethods
    {
        public static async Task<T> DeserializeObject<T>(this HttpResponseMessage httpResponse)
        {
            string json = await httpResponse.Content.ReadAsStringAsync();

            return JsonConvert.DeserializeObject<T>(json);
        }
        
        public static async Task<IEnumerable<T>> DeserializeCollection<T>(this HttpResponseMessage httpResponse)
        {
            string json = await httpResponse.Content.ReadAsStringAsync();

            return JsonConvert.DeserializeObject<List<T>>(json);
        }
    }
}