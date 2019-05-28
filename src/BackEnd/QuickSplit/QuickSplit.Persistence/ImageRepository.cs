using System;
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
        private readonly string[] ValidFormats = {"png", "jpeg", "jpg"};
        private string ImageDir => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, FolderName);

        public string FolderName { get; set; } = "Avatars";

        public Stream GetImageStream(int id)
        {
            string basePath = ImageDir;
            string imagePath = Directory
                                   .GetFiles(basePath)
                                   .FirstOrDefault(path => FileIsUserAvatar(id, path))
                               ?? basePath + "/default.png";

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

        public async void AddImageFromStream(int id, Stream image, string imageExt)
        {
            string avatarPath = GetImagePath(id, imageExt);
            using (var fs = new FileStream(avatarPath, FileMode.OpenOrCreate))
            {
                await image.CopyToAsync(fs);
            }
        }

        public async void AddImageFromBase64(int id, string image, string imageExt)
        {
            string avatarPath = GetImagePath(id, imageExt);
            using (var fs = new FileStream(avatarPath, FileMode.OpenOrCreate))
            {
                byte[] bytes = Convert.FromBase64String(image);
                await fs.ReadAsync(bytes);
            }
        }
        
        private string GetImagePath(int id, string imageExt)
        {
            string ext = imageExt.Split('/').Last();
            if (!FormatIsValid(ext))
            {
                throw new InvalidCommandException("Formato de imagen invalido.");
            }

            Directory.CreateDirectory(ImageDir);
            DeleteImage(id);
            string avatarPath = ImageDir + $"/{id}.{ext}";
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
    }
}