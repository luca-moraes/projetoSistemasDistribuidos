namespace Domain.Infrastructure;

public class TextWriterLogger : ILogger
{
    private readonly TextWriter _output;

    public TextWriterLogger(TextWriter output)
    {
        _output = output;
    }
    public void log(ILogger.LogType type, string topic, string msg)
    {
        _output.WriteLine("{0:dd-MM-yy HH:mm:ss zz} {1,-5} {2}: {3,-30}", DateTime.Now, "["+type.ToString()+"]", topic.ToUpper(), msg);
    }
}