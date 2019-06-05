using System;
using System.IO;
using System.Threading.Tasks;

namespace QuickSplit.Application.Interfaces
{
    public interface IImageRepository
    {
        string FolderName { get; set; }
        
        int ImageQualityRatio { get; set; }
        
        Stream GetImageStream(int id);
        
        Task<string> GetImageBase64(int id);

        void AddImageFromStream(int id, Stream image);
        
        void AddImageFromBase64(int id, string image);

        void DeleteImage(int image);
    }
}