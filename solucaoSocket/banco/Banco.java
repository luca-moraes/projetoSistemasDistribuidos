public package solucaoMPI;

import static spark.Spark.*;

public class Banco{
    public static void main(String[] args) {
        port(4567);

        get("/api/data/:id", (request, response) -> {
            // Obtém o ID da requisição
            String idParam = request.params(":id");

            // Verifica se o parâmetro de ID é um número inteiro
            try {
                int id = Integer.parseInt(idParam);
                if (id > 0) {
                    return "ID válido: " + id;
                } else {
                    response.status(400); // Define o status da resposta para "Bad Request"
                    return "ID inválido. Deve ser um número inteiro maior que zero.";
                }
            } catch (NumberFormatException e) {
                response.status(400); // Define o status da resposta para "Bad Request"
                return "ID inválido. Deve ser um número inteiro.";
            }
        });

        post("/api/data", (request, response) -> {
            String body = request.body(); // Obtém o corpo da requisição
            String idParam = request.queryParams("id"); // Obtém o parâmetro "id" do corpo da requisição

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    if (id > 0) {
                        return "ID válido: " + id;
                    } else {
                        response.status(400);
                        return "ID inválido. Deve ser um número inteiro maior que zero.";
                    }
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "ID inválido. Deve ser um número inteiro.";
                }
            } else {
                response.status(400);
                return "ID não fornecido no corpo da requisição.";
            }
        });
    }
}