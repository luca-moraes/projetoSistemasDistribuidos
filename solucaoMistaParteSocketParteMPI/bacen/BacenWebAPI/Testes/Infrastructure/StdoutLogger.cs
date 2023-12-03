using System;
using System.IO;
using Domain.Infrastructure;
using NUnit.Framework;

namespace Testes.Domain.Infrastructure;

public class Logger
{
    private TextWriterLogger _logger = null!;
    private StringWriter _writer = null!;

    [SetUp]
    public void setup()
    {
        _writer = new StringWriter();
        _logger = new TextWriterLogger(_writer);
    }

    [Test]
    public void imprimeMensagemDeLog()
    {
        
        _logger.log(ILogger.LogType.Info, "topico", "mensagem");
        Assert.True(_writer.ToString().Contains("topico", StringComparison.OrdinalIgnoreCase));
        Assert.True(_writer.ToString().Contains("mensagem", StringComparison.OrdinalIgnoreCase));
        Assert.True(_writer.ToString().Contains("INFO", StringComparison.OrdinalIgnoreCase));
    }
    
    
}