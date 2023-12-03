namespace Domain.Infrastructure.MailService;

public class GmailConfigCode : IGmailConfig
{
    public const string senha = "pkoveujriixdtoim";
    public const string email = "carreiradocentefei@gmail.com";
    public const string nome = "Carreira Docente";

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