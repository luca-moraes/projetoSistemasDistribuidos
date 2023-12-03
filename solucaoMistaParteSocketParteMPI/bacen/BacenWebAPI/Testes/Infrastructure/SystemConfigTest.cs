using System;
using Domain.Infrastructure;
using NUnit.Framework;

namespace Testes.Domain.Infrastructure;

public class SystemConfigTest
{
    private string _basePath = null!;
    [SetUp]
    protected void setup()
    {
        _basePath = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
    }

    [Test]
    public void testSystemConfig()
    {
        SystemConfig _systemConfig = new SystemConfig();
        
        Assert.True(_basePath.Equals(_systemConfig.UsersDocBasePath));
        
        Console.WriteLine(_systemConfig.UsersDocBasePath);
    }
}