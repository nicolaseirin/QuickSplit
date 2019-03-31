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
            set
            {
                ValidateNotNullOrEmpty(value, "LastName");
                lastName = value;
            }
        }

        public string Mail
        {
            get => mail;
            set
            {
                if (IsValidMailString(value))
                {
                    mail = value;
                }
                else
                {
                    throw new DomainException($"User mail isn't valid");
                }
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

        private bool IsValidMailString(string value)
        {
            Regex isValidMail = new Regex(@"^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$");
            return isValidMail.IsMatch(value);
        }
        
        private void ValidateNotNullOrEmpty(string value, string propertyName)
        {
            if(string.IsNullOrWhiteSpace(value))
                throw  new DomainException($"{propertyName} is required");
        }
    }
}