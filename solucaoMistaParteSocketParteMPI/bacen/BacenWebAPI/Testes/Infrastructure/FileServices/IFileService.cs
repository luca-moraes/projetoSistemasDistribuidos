namespace Domain.Infrastructure.FileServices;

public interface IFileService
{
    Stream loadDocContent(string path);
    bool saveDocContent(string path, Stream content);
    bool deleteDoc(string path);
    bool updateDocContent(string path, Stream content);
    bool deletePath(string path);
}