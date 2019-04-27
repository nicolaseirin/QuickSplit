using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace QuickSplit.Tests.Integration
{
    public static class HttpClientExtensionMethods
    {
        public static async Task<HttpResponseMessage> PostObjectAsync(this HttpClient httpClient, string url, object toSerialize)
        {
            ByteArrayContent byteContent = Serialize(toSerialize);
            
            return await httpClient.PostAsync(url, byteContent);
        }
        
        public static async Task<HttpResponseMessage> PutObjectAsync(this HttpClient httpClient, string url, object toSerialize)
        {
            ByteArrayContent byteContent = Serialize(toSerialize);

            return await httpClient.PutAsync(url, byteContent);
        }

        private static ByteArrayContent Serialize(object toSerialize)
        {
            string json = JsonConvert.SerializeObject(toSerialize);
            byte[] buffer = System.Text.Encoding.UTF8.GetBytes(json);

            var byteContent = new ByteArrayContent(buffer);
            byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
            return byteContent;
        }
    }
}