namespace Domain.Infrastructure.Helpers.Exceptions;

public class DirectoryNullOrEmptyException : Exception
{
    public DirectoryNullOrEmptyException(string? message) : base(message)
    {
        
    }
}