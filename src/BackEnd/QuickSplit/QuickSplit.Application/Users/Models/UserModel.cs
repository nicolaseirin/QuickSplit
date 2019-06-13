using QuickSplit.Domain;

namespace QuickSplit.Application.Users.Models
{
    public class UserModel
    {
        public UserModel()
        {
        }

        public UserModel(User user)
        {
            Id = user.Id;
            Name = user.Name;
            LastName = user.LastName;
            Mail = user.Mail;
        }
        
        public int Id { get; set; }
        
        public string Name { get; set; }
        
        public string LastName { get; set; }
        
        public string Mail { get; set; }

        public string Avatar => $"users/{Id}/avatars";
    }
}