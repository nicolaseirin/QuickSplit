using System;
using System.IO;
using System.Threading.Tasks;

namespace QuickSplit.Application.Interfaces
{
    public interface IImageRepository
    {
        string FolderName { get; set; }
        
        Stream GetImageStream(int id);
        
        Task<string> GetImageBase64(int id);

        void AddImageFromStream(int id, Stream image, string imageExt);
        
        void AddImageFromBase64(int id, string image, string imageExt);

        void DeleteImage(int image);
    }
}