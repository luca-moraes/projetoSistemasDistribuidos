using Domain.Domain.Atividades;
using Domain.Infrastructure.FileServices;
using NUnit.Framework;
using Testes.Domain.Helpers.ReadyPatternFactories;

namespace Testes.Domain.Infrastructure.FileServices;

public class FileResolverTests
{
    private DocumentoComprobatorio _documentoComprobatorio = null!;
    private string _fileFullPath = "teste/doc.txt"; 

    [SetUp]
    public void setup()
    {
        _documentoComprobatorio = new DocumentoComprobatorioPadraoParaTestes()
            .create(fileName:_fileFullPath);
    }

    [Test]
    public void testFileResolver()
    {
        FileResolver _fileResolver = new FileResolver();
        string _response = _fileResolver.getDocumentoComprobatorioFullPath(_documentoComprobatorio);
        
        Assert.False(string.IsNullOrEmpty(_response));
        Assert.True(_response.Contains(_fileFullPath));
    }
}