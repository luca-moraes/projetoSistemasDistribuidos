using System.Net;
using System.Net.Mail;

namespace Domain.Infrastructure.MailService;

public class GMailService : IMailService
{
    private readonly GMailConfig _config;

    public GMailService(GMailConfig config)
    {
        _config = config;
    }

    public GMailService(IGmailConfig gmailConfig)
    {
        _config = gmailConfig.build();
    }

    public void sendMail(string emailTo, string subject, string body)
    {
        var fromAddress = new MailAddress(_config.fromMail, _config.fromName);
        string fromPassword = _config.password;
        var toAddress = new MailAddress(emailTo);
        var smtp = new SmtpClient
        {
            Host = "smtp.gmail.com",
            Port = 587,
            EnableSsl = true,
            DeliveryMethod = SmtpDeliveryMethod.Network,
            UseDefaultCredentials = false,
            Credentials = new NetworkCredential(fromAddress.Address, fromPassword)
        };
        using (var message = new MailMessage(fromAddress, toAddress)
               {
                   Subject = subject,
                   Body = body,
                   IsBodyHtml = true
               })
        {
            smtp.Send(message);
        }
    }
}

public class GMailConfig
{
    public string fromMail { get; set; } = null!;
    public string fromName { get; set; } = null!;

    public string password { get; set; } = null!;
}
