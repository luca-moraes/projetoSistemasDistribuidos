namespace BacenWebAPI.Data;

[Serializable]
public class ClientData
{
    public string nomeInstituicao { set; get; } = "";
    public int numeroConta { get; set; } = 0;

    public ClientData(string nomeInstituicao, int numeroConta)
    {
        this.nomeInstituicao = nomeInstituicao;
        this.numeroConta = numeroConta;
    }

}