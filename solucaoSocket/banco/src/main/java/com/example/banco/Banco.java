package com.example.banco;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.net.URL;


class Cliente{
    int clienteId;
    float saldo;

    public Cliente(int clienteId, float saldo) {
        this.clienteId = clienteId;
        this.saldo = saldo;
    }

    public void descontarSaldo(float valor) {
        this.saldo -= valor;
    }
}

class ClienteDestino{
    String nomeInstituicao;
    int numeroConta;
}

public class Banco{
    public static void main(String[] args) {
        HashMap<Integer, Cliente> clientes = new HashMap<>();
        clientes.put(11, new Cliente(11, 1000.0f));

        port(4567);

        get("/FazerTransferencia", (req, res) -> {
            try {
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n REQ: " + req.queryString() + " \n\n\n\n\n\n\n\n\n\n\n\n\n\n");

                int clienteId = Integer.parseInt(req.queryParams("clienteId"));
                float valorTransferencia = Float.parseFloat(req.queryParams("valorTransferencia"));
                String chaveDestino = req.queryParams("chaveDestino");

                ClienteDestino clienteDestinoPix = new ClienteDestino();
                clienteDestinoPix.nomeInstituicao = "Banco do Brasil";
                clienteDestinoPix.numeroConta = 22;

                Cliente clienteOrigem = clientes.get(clienteId);
                clienteOrigem.descontarSaldo(valorTransferencia);

                System.out.println("Valor enviado para o cliente " + clienteDestinoPix.numeroConta +
                    " da instituição " + clienteDestinoPix.nomeInstituicao);

                return 200;
            } catch (Exception e) {
                e.printStackTrace();
                return 404;
            }
        });
    }

    private static ClienteDestino obterClienteDestino(String chaveDestino) {
        String apiUrl = "http://localhost:5288/Bacen?chave=" + chaveDestino;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Gson gson = new Gson();
                // ClienteDestino clienteDestinoPix = gson.fromJson(response.toString(), ClienteDestino.class);

                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n Parsed JSON: " + response.toString() + " \n\n\n\n\n\n\n\n\n\n\n\n\n\n");

                JsonParser parser = new JsonParser();
                JsonObject jsonResponse = parser.parse(response.toString()).getAsJsonObject();

                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n Parsed JSON: " + jsonResponse.toString() + " \n\n\n\n\n\n\n\n\n\n\n\n\n\n");

                String instituicaoNome = jsonResponse.getAsJsonObject("clienteData").get("nomeInstituicao").getAsString();
                int clienteDestinoId = jsonResponse.getAsJsonObject("clienteData").get("numeroConta").getAsInt();

                ClienteDestino clienteDestinoPix = new ClienteDestino();
                clienteDestinoPix.nomeInstituicao = instituicaoNome;
                clienteDestinoPix.numeroConta = clienteDestinoId;

                return clienteDestinoPix;
            } else {
                System.out.println("Falha na chamada à API externa: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}