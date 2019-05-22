using System.Linq;
using System.Threading;
using QuickSplit.Application.Users.Queries;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Application
{
    public class PasswordIsValidQueryTests : CommandsTestBase
    {
        [Fact]
        public void HashedPasswordTrueTest()
        {
            var password = "Password123";
            string hashed = PasswordHasher.Hash(password);
            var user = new User()
            {
                Id = 1,
                Name = "John",
                Mail = "mail@gmail.com",
                Password = hashed
            };
            Users.Add(user);
            Context.SaveChanges();

            var query = new PasswordIsValidQuery()
            {
                Mail = "mail@gmail.com",
                Password = password
            };
            var handler = new PasswordIsValidQueryHandler(Context, PasswordHasher);
            var result =  handler.Handle(query, CancellationToken.None).Result;

            Assert.NotNull(result);
        }
        
        [Fact]
        public void HashedPasswordFalseTest()
        {
            var password = "Password123";
            string hashed = PasswordHasher.Hash(password);
            var user = new User()
            {
                Name = "John",
                Mail = "mail@gmail.com",
                Password = hashed
            };
            Users.Add(user);
            Context.SaveChanges();
            
            var query = new PasswordIsValidQuery()
            {
                Mail = "mail@gmail.com",
                Password = "Password124"
            };
            var handler = new PasswordIsValidQueryHandler(Context, PasswordHasher);
            var result =  handler.Handle(query, CancellationToken.None).Result;

            Assert.Null(result);
        }
        
        [Fact]
        public void NonExistantUserTest()
        {
            var password = "Password123";
            string hashed = PasswordHasher.Hash(password);

            var query = new PasswordIsValidQuery()
            {
                Mail = "mail2@gmail.com",
                Password = password
            };
            var handler = new PasswordIsValidQueryHandler(Context, PasswordHasher);
            var result =  handler.Handle(query, CancellationToken.None).Result;

            Assert.Null(result);
        }
    }
}