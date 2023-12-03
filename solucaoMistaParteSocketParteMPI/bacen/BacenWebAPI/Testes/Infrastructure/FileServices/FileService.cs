using Domain.Infrastructure.Helpers.Exceptions;

namespace Domain.Infrastructure.FileServices;

public class FileService : IFileService
{
    public bool saveDocContent(string path, Stream content)
    {
        string directoryName = Path.GetDirectoryName(path)
                               ?? throw new DirectoryNullOrEmptyException($"Diretório {path} não encontrado ou nulo!");

        if (!Directory.Exists(directoryName))
            Directory.CreateDirectory(directoryName);

        if (File.Exists(path))
            throw new FileAlreadyExistsException($"Can't override file");

        using FileStream file = new FileStream(path, FileMode.Create);
        content.CopyTo(file);

        return File.Exists(path);
    }
    
    public Stream loadDocContent(string path)
    {
        if (!File.Exists(path))
            throw new FileNotFoundException($"Missed file");
                
        using FileStream fileStream = new FileStream(path, FileMode.Open);

        MemoryStream memoryStream = new MemoryStream();
        fileStream.CopyTo(memoryStream);
        
        fileStream.Flush();
        memoryStream.Position = 0;

        return memoryStream;
    }

    public bool deleteDoc(string path)
    {
        if (!File.Exists(path))
            throw new FileNotFoundException($"Missed file");

        File.Delete(path);

        return !File.Exists(path);
    }

    public bool deletePath(string path)
    {
        if (!Directory.Exists(path)) 
            throw new DirectoryNotFoundException($"Diretório {path} não encontrado!");
        
        Directory.Delete(path,true);
        
        return !Directory.Exists(path);
    }

    public bool updateDocContent(string path, Stream content)
    {
        deleteDoc(path);
        
        return saveDocContent(path, content);
    }
}