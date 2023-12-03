using System;
using System.IO;
using System.Text;
using Domain.Domain.Atividades;
using Domain.Infrastructure.FileServices;
using Domain.Infrastructure.Helpers.Exceptions;
using NUnit.Framework;
using Testes.Domain.Helpers.ReadyPatternFactories;

namespace Testes.Domain.Infrastructure.FileServices;

public class FileServiceTests
{
    private DocumentoComprobatorio _documentoComprobatorio = null!;
    private FileService _fileService = new FileService();
    private FileResolver _fileResolver = new FileResolver();
    private string _fileFullPath = null!;
    private string _textContent = "teste";
    private string _response = null!;

    [SetUp]
    public void setup()
    {
        _fileFullPath = new StringBuilder().Append("teste/" + new Random().Next(1000, 9999) + ".txt").ToString();
        
        _documentoComprobatorio = new DocumentoComprobatorioPadraoParaTestes()
            .create(fileName:_fileFullPath);
        
        _response = _fileResolver.getDocumentoComprobatorioFullPath(_documentoComprobatorio);
    }

    [Test]
    public void testFileServiceSaveFile()
    {
        MemoryStream _memoryStream = new MemoryStream(new UnicodeEncoding().GetBytes(_textContent));

        Assert.True(_fileService.saveDocContent(_response, _memoryStream));
    }

    [Test]
    public void testFileServiceUpdatefile()
    {
        this.testFileServiceSaveFile();
        
        _textContent = "new content update";
        MemoryStream _memoryStream = new MemoryStream(new UnicodeEncoding().GetBytes(_textContent));

        Assert.True(_fileService.updateDocContent(_response, _memoryStream));
        
        Assert.True(
            Encoding.Unicode.GetString(
        (_fileService.loadDocContent(_response) as MemoryStream)?
            .ToArray() ?? throw new InvalidOperationException())
            .Equals(_textContent)
        );
    }

    [Test]
    public void testFileServiceLoadFile()
    {
        try
        {
            this.testFileServiceSaveFile();
        }
        catch (Exception e)
        {
            Console.WriteLine($"arquivo ja existente {e}");
        }

        Assert.True(
            Encoding.Unicode.GetString(
        (_fileService.loadDocContent(_response) as MemoryStream)?
            .ToArray() ?? throw new InvalidOperationException())
            .Equals(_textContent)
        );
    }

    [Test]
    public void testFileServiceLoadFileException()
    {
        Assert.Throws<FileNotFoundException>(()=>
        {
            _fileService.loadDocContent("/notExistDirectory/notExistFile.txt");
        });
    }

    [Test]
    public void testFileServiceSaveFileException()
    {
        Assert.Throws<DirectoryNullOrEmptyException>(() =>
        {
            _fileService.saveDocContent("", new MemoryStream());
        });
    }

    [Test]
    public void testFileServiceSaveFileOverride()
    {
        _fileService.saveDocContent(_response, new MemoryStream());
        
        Assert.Throws<FileAlreadyExistsException>(() =>
        {
            _fileService.saveDocContent(_response, new MemoryStream());
        });
    }

    [Test]
    public void testFileServiceDeleteFile()
    {
        _fileService.saveDocContent(_response, new MemoryStream());
        
        Assert.True(_fileService.deleteDoc(_response));
    }

    [Test]
    public void testFileServiceDeleteFileException()
    {
        Assert.Throws<FileNotFoundException>(() => { _fileService.deleteDoc("/dontExist/dontExiste.txt"); });
    }

    [Test]
    public void testFileServiceDeleteDirectory()
    {
        var _directory = "tmp/deleteTestDirectory/";
        _fileService.saveDocContent(_directory + "teste.txt", new MemoryStream());
        
        Assert.True(_fileService.deletePath(_directory));
    }

    [Test]
    public void testFileServiceDeleteDirectoryException()
    {
        Assert.Throws<DirectoryNotFoundException>(()=>
        {
            _fileService.deletePath("/tmp/thisPathDontExist/");
        });
    }
}
