namespace Domain.Infrastructure.MailService;

public interface IMailService
{
    public void sendMail(string emailTo, string subject, string body);
}