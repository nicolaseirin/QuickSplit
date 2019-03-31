using System;
using System.Runtime.Serialization;

namespace QuickSplit.Application.Exceptions
{
    public class InternalErrorException : ApplicationException
    {
        public InternalErrorException()
        {
        }

        public InternalErrorException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }

        public InternalErrorException(string message) : base(message)
        {
        }

        public InternalErrorException(string message, Exception innerException) : base(message, innerException)
        {
        }
    }
}