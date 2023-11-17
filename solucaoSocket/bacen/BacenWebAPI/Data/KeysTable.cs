using System.Collections;
using System.Text;
using Microsoft.Extensions.Primitives;

namespace BacenWebAPI.Data;

public class KeysTable
{
    public Dictionary<string, ClientData> dictionaryClients = new Dictionary<string, ClientData>();

    public KeysTable()
    {
        IList<string> instituicoes = new List<string>() { "a", "b", "c" };

        foreach (string inst  in instituicoes)
        {
            for (int i = 0; i < 5000; i++)
            {
                ClientData client = new ClientData(inst, i);
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.Append(inst);
                stringBuilder.Append(i);

                string chave = stringBuilder.ToString();
                
                dictionaryClients.Add(chave,client);
            }
        }
    }
}