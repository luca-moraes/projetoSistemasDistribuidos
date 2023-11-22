using System.Net;
using BacenWebAPI.Data;
using Microsoft.AspNetCore.Mvc;

namespace BacenWebAPI.Controllers;

[ApiController]
[Route("[controller]")]
public class BacenController : ControllerBase
{
    private KeysTable tabelaChaves = new KeysTable();

    private ClientData? buscarCliente(string chave)
    {
        ClientData cliente = tabelaChaves.dictionaryClients[chave];

        return cliente ?? null;
    } 

    [HttpGet(Name = "ConsultarChave")]
    public ClienteResponseOutput consultarChave([FromQuery(Name = "chave")] string chave)
    {
        try
        {
            ClientData? cliente = buscarCliente(chave);
            
            if (cliente != null)
            {
                return new ClienteResponseOutput()
                {
                    clienteData = cliente
                };
            }

            var str = $"Não foi encontrado cliente com a chave '{chave}'!";
            throw new BadHttpRequestException(str,404);
        }
        catch (Exception e)
        {
            var str = $"Não foi encontrado cliente com a chave '{chave}'!";
            throw new BadHttpRequestException(str,404);
        }
    }
}