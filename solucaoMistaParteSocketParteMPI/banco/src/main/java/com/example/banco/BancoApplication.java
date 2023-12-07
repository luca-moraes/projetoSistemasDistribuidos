package com.example.banco;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
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
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class BancoApplication {
	@Value("${server.port}")
	private int portaPadrao;

	public static HashMap<Integer, Cliente> clientes = new HashMap<>();

	public static void main(String[] args) {
		fillHash();
		SpringApplication.run(BancoApplication.class, args);
	}

	public static int direcionamentoPortas(String chave){
		int porta = 8080;
		switch (chave){
			case "a":
				porta = 8081;
				break;
			case "b":
				porta = 8082;
				break;
			case "c":
				porta = 8083;
				break;
		}
		return porta;
	}

	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return factory -> factory.setPort(portaPadrao);
	}

	@GetMapping("/saldoTotal")
	public BigDecimal saldoTotal(){
		// teste req: http://localhost:8080/saldoTotal

		BigDecimal saldoTotal = BigDecimal.ZERO;

		for(int i = 1; i <= 5000; i++){
			saldoTotal = saldoTotal.add(clientes.get(i).saldo);
		}

		return saldoTotal;
	}

	@GetMapping("/resetarSaldos")
	public int resetarSaldos(){
		try {			
			// teste req: http://localhost:8080/resetarSaldos
			for(int i = 1; i <= 5000; i++){
				clientes.get(i).saldo = new BigDecimal("100.00");
			}
			// System.out.println("Saldos resetados!");

			return 200;
		}catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/receberValor")
	public int receberValor(@RequestParam(value = "chaveOrigem") String chaveOrigem, @RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "destinoId") int destinoId){
		try {
			// System.out.println("\nREQ rec: " + chaveOrigem);
			// teste req: http://localhost:8080/receberValor?chaveOrigem=a22&valor=12&destinoId=11

			Cliente clienteDestinoPix = clientes.get(destinoId);

			clienteDestinoPix.adicionarSaldo(valor);

			// System.out.println("Valor " + valor + " recebido de " + chaveOrigem + " para " + destinoId + " saldo=" + clienteDestinoPix.saldo);

			return 200;
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/transferenciaInterna")
	public int transferenciaInterna(@RequestParam(value = "clienteId") int clienteId, @RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "clienteDestino") int clienteDestino){
		try {
			// System.out.println("\nREQ int: " + clienteDestino);
			// teste req: http://localhost:8080/transferenciaInterna?clienteId=11&valor=12&clienteDestino=21

			Cliente clienteDestinoPix = clientes.get(clienteDestino);
			Cliente clienteOrigem = clientes.get(clienteId);

			if(clienteOrigem.saldo.compareTo(valor) < 0){
				// System.out.println("Saldo cliente " + clienteOrigem.clienteId + "="+ clienteOrigem.saldo + " menor que valor " + valor);
				return 405;
			}

			clienteOrigem.descontarSaldo(valor);
			clienteDestinoPix.adicionarSaldo(valor);

			// System.out.println("Valor " + valor + " enviado de " + clienteOrigem.clienteId + " saldo:" + clienteOrigem.saldo + " para " + clienteDestinoPix.clienteId + " saldo:" + clienteDestinoPix.saldo);
			return 200;
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/transferenciaExterna")
	public int transferenciaExterna(@RequestParam(value = "clienteId") int clienteId, @RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "chaveDestino") String chaveDestino){
		// @GetMapping("/FazerTransferencia/{clienteId}/{value}/{key}")
		// public int fazerTransferencia(@PathVariable int clienteId, @PathVariable float value, @PathVariable String key) {
		try {
			// System.out.println("\nREQ ext: " + chaveDestino);
			// teste req: http://localhost:8080/FazerTransferencia/11/12/a1
			// teste req: http://localhost:8080/transferenciaExterna?clienteId=11&valor=12&chaveDestino=a1

			// ClienteDestino clienteDestinoPix = new ClienteDestino();
			// clienteDestinoPix.nomeInstituicao = "Banco do Brasil";
			//clienteDestinoPix.numeroConta = 22;

			ClienteDestino clienteDestinoPix = obterClienteDestino(chaveDestino);
			Cliente clienteOrigem = clientes.get(clienteId);

			if(clienteOrigem.saldo.compareTo(valor) < 0){
				// System.out.println("Saldo cliente " + clienteOrigem.clienteId + "="+ clienteOrigem.saldo + " menor que valor " + valor);
				return 405;
			}


			if(chamarApiRecebimento("a"+clienteOrigem.clienteId, valor, clienteDestinoPix.numeroConta, clienteDestinoPix.nomeInstituicao) == 200){
				// System.out.println("enviado!");
				clienteOrigem.descontarSaldo(valor);
				return 200;
			}else{
				// clienteOrigem.adicionarSaldo(valor);
				return 404;
			}

			// System.out.println("Valor " + valor + " enviado para o cliente " + clienteDestinoPix.numeroConta + " da instituição " + clienteDestinoPix.nomeInstituicao);
			// System.out.println("Saldo atual:" + clientes.get(clienteId).saldo);
			// return 200;
		} catch (Exception e) {
			e.printStackTrace();
			return 404;
		}
	}

	private static ClienteDestino obterClienteDestino(String chaveDestino) {
		// String apiUrl = "http://10.210.23.228:5288/Bacen?chave=" + chaveDestino;
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

				// System.out.println("Parsed JSON: " + jsonResponse.toString());

				String instituicaoNome = jsonResponse.getAsJsonObject("clienteData").get("nomeInstituicao").getAsString();
				int clienteDestinoId = jsonResponse.getAsJsonObject("clienteData").get("numeroConta").getAsInt();

				ClienteDestino clienteDestinoPix = new ClienteDestino();
				clienteDestinoPix.nomeInstituicao = instituicaoNome;
				clienteDestinoPix.numeroConta = clienteDestinoId;

				return clienteDestinoPix;
			} else {
				// System.out.println("Falha na chamada à API externa: " + response.code());
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static int chamarApiRecebimento(String chaveOrig, BigDecimal valor, int destinoId, String bancoDestino) {

		String apiUrl = "http://localhost:" + direcionamentoPortas(bancoDestino) + "/receberValor?chaveOrigem=" + chaveOrig + "&valor=" + valor + "&destinoId=" + destinoId;

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
				return 200;
			} else {
				// System.out.println("Falha na chamada à API externa: " + response.code());
				return 404;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 404;
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
		for(int i = 1; i <= 5000; i++){
			clientes.put(i, new Cliente(i, new BigDecimal("100.00")));
		}
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
	BigDecimal saldo;

	public Cliente(int clienteId, BigDecimal saldo) {
		this.clienteId = clienteId;
		this.saldo = saldo;
	}

	public synchronized void descontarSaldo(BigDecimal valor) {
		if(this.saldo.compareTo(valor) >= 0){
			this.saldo = this.saldo.subtract(valor);
		}
	}

	public synchronized void adicionarSaldo(BigDecimal valor) {
    	this.saldo = this.saldo.add(valor);
	}
}

class ClienteDestino{
	String nomeInstituicao;
	int numeroConta;
}