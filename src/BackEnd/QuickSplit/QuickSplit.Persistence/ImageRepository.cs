using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Net;
using System.Threading.Tasks;
using MediatR;
using QuickSplit.Application.Exceptions;
using QuickSplit.Application.Interfaces;
using QuickSplit.Application.Users;

namespace QuickSplit.Persistence
{
    public class ImageRepository : IImageRepository
    {
        private const string OutputExt = "jpg";
        private readonly string[] ValidFormats = {"png", "jpeg", "jpg"};
        private string ImageDir => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, FolderName);

        public int ImageQualityRatio { get; set; } = 15;
        public string FolderName { get; set; } = "Avatars";

        public Stream GetImageStream(int id)
        {
            string basePath = ImageDir;
            string imagePath = Directory
                                   .GetFiles(basePath)
                                   .FirstOrDefault(path => FileIsUserAvatar(id, path))
                               ?? basePath + "/default.jpg";

            return new FileStream(imagePath, FileMode.Open);
        }

        public async Task<string> GetImageBase64(int id)
        {
            using (Stream stream = GetImageStream(id))
            {
                var buffer = new byte[1024 * 1024];
                await stream.ReadAsync(buffer);
                return Convert.ToBase64String(buffer);
            }
        }

        private bool FormatIsValid(string requestImageFormat)
        {
            return ValidFormats
                .Any(f => f.Equals(requestImageFormat, StringComparison.OrdinalIgnoreCase));
        }

        public void AddImageFromStream(int id, Stream image)
        {
            string avatarPath = GetImagePath(id);
            
            try
            {
                SaveJpeg(avatarPath, Image.FromStream(image), ImageQualityRatio);
            }
            catch (ArgumentException ex)
            {
                throw new InvalidCommandException("Imagen invalida");
            }
        }

        public void AddImageFromBase64(int id, string image)
        {
            string avatarPath = GetImagePath(id);
            using (var fs = new MemoryStream(Convert.FromBase64String(image)))
            {
                SaveJpeg(avatarPath, Image.FromStream(fs), ImageQualityRatio);
            }
        }

        private string GetImagePath(int id)
        { 
            Directory.CreateDirectory(ImageDir);
            DeleteImage(id);
            string avatarPath = ImageDir + $"/{id}.{OutputExt}";
            return avatarPath;
        }

        public void DeleteImage(int image)
        {
            foreach (string file in Directory.GetFiles(ImageDir).Where(f => FileIsUserAvatar(image, f)))
            {
                File.Delete(file);
            }
        }

        private static bool FileIsUserAvatar(int userId, string filePath)
        {
            return filePath
                .Split('/')
                .Last()
                .Split('.')
                .First()
                .Equals(userId.ToString(), StringComparison.OrdinalIgnoreCase);
        }
        
        private static void SaveJpeg(string path, Image img, int quality)
        {
            if (quality < 0 || quality > 100)
                throw new ArgumentOutOfRangeException("quality must be between 0 and 100.");


            // Encoder parameter for image quality 
            var qualityParam = new EncoderParameter(Encoder.Quality, quality);
            // Jpeg image codec 
            ImageCodecInfo jpegCodec = GetEncoderInfo("image/jpeg");

            var encoderParams = new EncoderParameters(1) {Param = {[0] = qualityParam}};
            img.Save(path, jpegCodec, encoderParams);
        }
        
        private static ImageCodecInfo GetEncoderInfo(string mimeType)
        {
            // Get image codecs for all image formats 
            ImageCodecInfo[] codecs = ImageCodecInfo.GetImageEncoders();

            // Find the correct image codec 
            return codecs.FirstOrDefault(t => t.MimeType == mimeType);
        }
    }
}