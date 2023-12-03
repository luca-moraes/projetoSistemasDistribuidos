using Domain.Infrastructure.MailService;
using NUnit.Framework;

namespace Testes.Domain.Infrastructure;

public class MailSenderTests
{
    private GMailService _service = null!;

    [SetUp]
    public void setup()
    {
        _service = new GMailService(new GMailConfig()
        {
            password = "****************",
            fromMail = "testeSd@gmail.com",
            fromName = "testeSd",
        });
    }
    [Test]
    public void enviaEmail()
    {
        Assert.DoesNotThrow(() =>
        {
            _service.sendMail("testeSd@gmail.com","testeSd","<h1>teste</h1>");
        });
        
    }
    
}
