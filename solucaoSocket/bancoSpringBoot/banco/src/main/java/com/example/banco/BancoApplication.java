package com.example.banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class BancoApplication {
	public static HashMap<Integer, Cliente> clientes = new HashMap<>();

	public static void main(String[] args) {
		fillHash();
		SpringApplication.run(BancoApplication.class, args);
	}

	@GetMapping("/FazerTransferencia/{clienteId}/{value}/{key}")
	public int fazerTransferencia(@PathVariable int clienteId, @PathVariable float value, @PathVariable String key) {
		try {
			System.out.println("\nREQ: " + key);
			// teste req: http://localhost:8080/FazerTransferencia/11/12/a1

			// ClienteDestino clienteDestinoPix = new ClienteDestino();
			// clienteDestinoPix.nomeInstituicao = "Banco do Brasil";
			//clienteDestinoPix.numeroConta = 22;

			ClienteDestino clienteDestinoPix = obterClienteDestino(key);

			Cliente clienteOrigem = clientes.get(clienteId);
			clienteOrigem.descontarSaldo(value);

			System.out.println("Valor " + value + " enviado para o cliente " + clienteDestinoPix.numeroConta + " da instituição " + clienteDestinoPix.nomeInstituicao);

			return 200;
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
	}

	private static ClienteDestino obterClienteDestino(String chaveDestino) {
		String apiUrl = "https://localhost:7048/Bacen?chave=" + chaveDestino;

		OkHttpClient client = new OkHttpClient.Builder()
				.sslSocketFactory(createUnsafeSSLSocketFactory(), new TrustAllManager())
				.hostnameVerifier((hostname, session) -> true)
				.build();

		Request request = new Request.Builder()
				.url(apiUrl)
				.build();

		try {
			Response response = client.newCall(request).execute();

			if (response.isSuccessful()) {
				String responseBody = response.body().string();

				JsonParser parser = new JsonParser();
				JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();

				System.out.println("Parsed JSON: " + jsonResponse.toString());

				String instituicaoNome = jsonResponse.getAsJsonObject("clienteData").get("nomeInstituicao").getAsString();
				int clienteDestinoId = jsonResponse.getAsJsonObject("clienteData").get("numeroConta").getAsInt();

				ClienteDestino clienteDestinoPix = new ClienteDestino();
				clienteDestinoPix.nomeInstituicao = instituicaoNome;
				clienteDestinoPix.numeroConta = clienteDestinoId;

				return clienteDestinoPix;
			} else {
				System.out.println("Falha na chamada à API externa: " + response.code());
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static SSLSocketFactory createUnsafeSSLSocketFactory() {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException("Error creating SSL socket factory", e);
		}
	}

	private static class TrustAllManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	public static void fillHash(){
		clientes.put(11, new Cliente(11, 1000.0f));
	}

	@GetMapping("/teste")
	public String testeCreate(@RequestParam(value = "key", defaultValue = "a1") String chave, @RequestParam(value = "k2", defaultValue = "a2") String c2){
		return String.format("Response: %s - %s", chave, c2);
		// teste req: http://localhost:8080/teste?key=a55&k2=a44
	}

	@GetMapping("/testePath/{id}/{id2}")
	public String testePath(@PathVariable Integer id, @PathVariable Integer id2){
		// id = id == null ? 3 : id;
		// id2 = id2 == null ? 4 : id2;

		return String.format("Response: %d - %d", id, id2);
		// teste req: http://localhost:8080/teste?key=a55&k2=a44
	}
}

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
