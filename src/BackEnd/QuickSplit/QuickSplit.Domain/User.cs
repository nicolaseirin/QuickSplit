using System.Text.RegularExpressions;

namespace QuickSplit.Domain
{
    public class User
    {
        private string name;
        private string lastName;
        private string mail;
        private string password;

        public int Id { get; set; }

        public string Name
        {
            get => name;
            set
            {
                ValidateNotNullOrEmpty(value, "Name");
                name = value;
            }
        }

        public string LastName
        {
            get => lastName;
            set => lastName = value ?? throw new DomainException($"LastName is required");
        }

        public string Mail
        {
            get => mail;
            set
            {
                IsValidMailString(value, "Mail");
                mail = value;
            }
        }

        public string Telephone { get; set; }

        public string Password
        {
            get => password;
            set
            {
                ValidateNotNullOrEmpty(value, "Password");
                password = value;
            }
        }

        private void IsValidMailString(string value, string propertyName)
        {
            ValidateNotNullOrEmpty(value, propertyName);
            Regex isValidMail = new Regex(@"^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$");
            if(!isValidMail.IsMatch(value))
                throw new DomainException($"{value} is not a valid email address");
            
        }
        
        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if(string.IsNullOrWhiteSpace(value))
                throw  new DomainException($"{propertyName} is required");
        }
    }
}