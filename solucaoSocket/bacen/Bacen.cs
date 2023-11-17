using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;

using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Hosting;


namespace BacenAPI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) => Host.CreateDefaultBuilder(args).ConfigureWebHostDefaults(webBuilder =>
        {
            webBuilder.UseStartup<Startup>();
            webBuilder.UseUrls("http://localhost:5000"); // Define a porta e endereço do servidor
        });
    }
    
    public class Startup
    {
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddControllers();
        }

        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            app.UseRouting();

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }

    public class DataController : ControllerBase
    {
        [HttpPost("/api/data")]
        public ActionResult<string> Post([FromBody] string id)
        {
            if (!string.IsNullOrEmpty(id))
            {
                if (int.TryParse(id, out int parsedId))
                {
                    if (parsedId > 0)
                    {
                        return "ID válido: " + parsedId;
                    }
                    return BadRequest("ID inválido. Deve ser um número inteiro maior que zero.");
                }
                return BadRequest("ID inválido. Deve ser um número inteiro.");
            }
            return BadRequest("ID não fornecido no corpo da requisição.");
        }
    }
}
