namespace Domain.Infrastructure;

public interface ILogger
{
    public enum LogType
    {
        Info,
        Warn,
        Error,
    }

    public void log(LogType type, String topic, String msg);

}