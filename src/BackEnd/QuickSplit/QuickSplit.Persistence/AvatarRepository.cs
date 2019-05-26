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
    public class AvatarRepository : IAvatarRepository
    {
        private readonly string[] ValidFormats = {"png", "jpeg", "jpg"};
        private readonly string AvatarsDir  = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Avatars");
        
        public Stream GetAvatarStream(int userId)
        {
            string basePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "Avatars");
            string imagePath = Directory
                                   .GetFiles(basePath)
                                   .FirstOrDefault(path => FileIsUserAvatar(userId, path))
                               ?? basePath + "/default.png";

            return new FileStream(imagePath, FileMode.Open);
        }

        public async Task<string> GetAvatarBase64(int userId)
        {
            using (Stream stream = GetAvatarStream(userId))
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

        public async void SetAvatarFromStream(int userId, Stream avatar, string avatarExt)
        {
            string avatarPath = GetAvatarPath(userId, avatarExt);
            using (var fs = new FileStream(avatarPath, FileMode.OpenOrCreate))
            {
                await avatar.CopyToAsync(fs);
            }
        }

        public async void SetAvatarFromBase64(int userId, string avatar, string avatarExt)
        {
            string avatarPath = GetAvatarPath(userId, avatarExt);
            using (var fs = new FileStream(avatarPath, FileMode.OpenOrCreate))
            {
                byte[] bytes = Convert.FromBase64String(avatar);
                await fs.ReadAsync(bytes);
            }
        }
        
        private string GetAvatarPath(int userId, string avatarExt)
        {
            string ext = avatarExt.Split('/').Last();
            if (!FormatIsValid(ext))
            {
                throw new InvalidCommandException("Formato de imagen invalido.");
            }

            Directory.CreateDirectory(AvatarsDir);
            DeleteOldImage(userId);
            string avatarPath = AvatarsDir + $"/{userId}.{ext}";
            return avatarPath;
        }

        private void DeleteOldImage(int userId)
        {
            foreach (string file in Directory.GetFiles(AvatarsDir).Where(f => FileIsUserAvatar(userId, f)))
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