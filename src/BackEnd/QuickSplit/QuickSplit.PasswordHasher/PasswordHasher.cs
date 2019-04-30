using System;
using System.Security.Cryptography;
using System.Text;
using QuickSplit.Application.Interfaces;

namespace QuickSplit.PasswordHasher
{
    public class PasswordHasher : IPasswordHasher
    {
        public string Hash(string password)
        {
            {
                byte[] bytes = Encoding.Unicode.GetBytes(password);
                byte[] inArray = HashAlgorithm.Create("SHA1")?.ComputeHash(bytes);
                return Convert.ToBase64String(inArray);
            }
        }
    }
}