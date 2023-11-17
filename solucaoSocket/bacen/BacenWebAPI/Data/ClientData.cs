namespace BacenWebAPI.Data;

public class ClientData
{
    public string nomeInstituicao = "";
    public int numeroConta = 0;

    public ClientData(string nomeInstituicao, int numeroConta)
    {
        this.nomeInstituicao = nomeInstituicao;
        this.numeroConta = numeroConta;
    }

}