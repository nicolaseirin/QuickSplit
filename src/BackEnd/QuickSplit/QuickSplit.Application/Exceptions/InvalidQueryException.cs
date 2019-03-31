using System;
using System.Runtime.Serialization;

namespace QuickSplit.Application.Exceptions
{
    public class InvalidQueryException : ApplicationException
    {
        public InvalidQueryException()
        {
        }

        protected InvalidQueryException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }

        public InvalidQueryException(string message) : base(message)
        {
        }

        public InvalidQueryException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}