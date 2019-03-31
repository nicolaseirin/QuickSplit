using System;
using System.Runtime.Serialization;

namespace QuickSplit.Application.Exceptions
{
    public class InvalidCommandException : ApplicationException
    {
        public InvalidCommandException()
        {
        }

        protected InvalidCommandException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }

        public InvalidCommandException(string message) : base(message)
        {
        }

        public InvalidCommandException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}