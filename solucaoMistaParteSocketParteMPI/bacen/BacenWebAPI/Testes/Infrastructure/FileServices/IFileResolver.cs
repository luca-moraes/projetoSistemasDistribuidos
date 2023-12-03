using Domain.Domain.Atividades;

namespace Domain.Infrastructure.FileServices;


public interface IFileResolver
{
    string getDocumentoComprobatorioFullPath(DocumentoComprobatorio doc);
}