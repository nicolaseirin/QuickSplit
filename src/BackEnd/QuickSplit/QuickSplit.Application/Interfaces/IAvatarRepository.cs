using System.IO;
using System.Threading.Tasks;

namespace QuickSplit.Application.Interfaces
{
    public interface IAvatarRepository
    {
        Stream GetAvatarStream(int userId);
        
        Task<string> GetAvatarBase64(int userId);

        void SetAvatarFromStream(int userId, Stream avatar, string avatarExt);
        
        void SetAvatarFromBase64(int userId, string avatar, string avatarExt);
    }
}