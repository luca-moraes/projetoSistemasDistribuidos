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
            password = "pkoveujriixdtoim",
            fromMail = "carreiradocentefei@gmail.com",
            fromName = "Carreira Docente",
        });
    }
    [Test]
    public void enviaEmail()
    {
        Assert.DoesNotThrow(() =>
        {
            _service.sendMail("carreiradocentefei@gmail.com","teste","<h1>teste</h1>");
        });
        
    }
    
}