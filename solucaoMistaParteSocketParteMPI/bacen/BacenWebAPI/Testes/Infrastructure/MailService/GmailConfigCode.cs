namespace Domain.Infrastructure.MailService;

public class GmailConfigCode : IGmailConfig
{
    public const string senha = "**************";
    public const string email = "testeSd@gmail.com";
    public const string nome = "testeSd";

    public GMailConfig build()
    {
        return new GMailConfig()
        {
            fromMail = email,
            fromName = nome,
            password = senha
        };
    }
}
