using System;
using System.Diagnostics;
using QuickSplit.Domain;
using Xunit;

namespace QuickSplit.Test.Domain
{
    public class UserTest
    {   
        [Fact]
        public void EmptyOrNullNameTest()
        {
            User user = new User();
            Assert.Throws<DomainException>(() => 
                user.Name = null
            );
            Assert.Throws<DomainException>(() => 
                user.Name = ""
            );
        }
        
        [Fact]
        public void NullLastNameTest()
        {
            User user = new User();
            Assert.Throws<DomainException>(() => 
                user.LastName = null
            );
        }
        
        [Fact]
        public void EmptyOrNullPasswordTest()
        {
            User user = new User();
            Assert.Throws<DomainException>(() => 
                user.Password = null
            );
            Assert.Throws<DomainException>(() => 
                user.Password = ""
            );
        }
        
        [Fact]
        public void EmptyOrNullMailTest()
        {
            User user = new User();
            Assert.Throws<DomainException>(() => 
                user.Mail = null
            );
            Assert.Throws<DomainException>(() => 
                user.Mail = ""
            );
        }
        
        [Fact]
        public void InvalidMailFormatTest()
        {
            User user = new User();
            Assert.Throws<DomainException>(() => 
                user.Mail = "mailWithoutarroba"
            );
            Assert.Throws<DomainException>(() => 
                user.Mail = "mailWithou@tdotcom"
            );
        }
    }
}