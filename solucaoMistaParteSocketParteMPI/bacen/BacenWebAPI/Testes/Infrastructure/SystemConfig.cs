namespace Domain.Infrastructure;

public class SystemConfig : ISystemConfig
{
    public string UsersDocBasePath { get; init; } 

    public SystemConfig()
    {
        UsersDocBasePath = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
    }
}