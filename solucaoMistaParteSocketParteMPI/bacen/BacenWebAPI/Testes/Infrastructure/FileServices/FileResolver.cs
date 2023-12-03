using Domain.Domain.Atividades;

namespace Domain.Infrastructure.FileServices;

public class FileResolver : IFileResolver
{ 
    public string getDocumentoComprobatorioFullPath(DocumentoComprobatorio doc)
    {
        return "/tmp/" + doc.fileName;
    }
    
}