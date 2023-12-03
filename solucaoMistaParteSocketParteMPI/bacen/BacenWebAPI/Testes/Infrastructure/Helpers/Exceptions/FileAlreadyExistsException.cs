namespace Domain.Infrastructure.Helpers.Exceptions;

public class FileAlreadyExistsException : Exception
{
    public FileAlreadyExistsException(string? message) : base(message){}
}