using System;
using System.IO;

namespace ConsoleTests
{
    internal class Program
    {
        public static string ObterExtensaoDoArquivo(string nomeDoArquivo)
        {
            if (!string.IsNullOrEmpty(nomeDoArquivo))
            {
                // Use o método Path.GetExtension para obter a extensão do arquivo.
                string extensao = Path.GetExtension(nomeDoArquivo);

                // Remova o ponto (.) inicial da extensão, se houver.
                if (!string.IsNullOrEmpty(extensao) && extensao.StartsWith("."))
                {
                    extensao = extensao.Substring(1);
                }

                return extensao;
            }

            // Se o nome do arquivo for nulo ou vazio, retorne uma extensão vazia.
            return string.Empty;
        }


        public static void Main(string[] args)
        {
            //Console.Write( Directory.GetDirectories("~").ToString());

            Console.Write(Environment.GetFolderPath(Environment.SpecialFolder.UserProfile));


            string nomeDoArquivo = "documento.txt";
            string extensao = ObterExtensaoDoArquivo(nomeDoArquivo);
            Console.WriteLine("Extensão do arquivo: " + extensao);
        }
    }
}