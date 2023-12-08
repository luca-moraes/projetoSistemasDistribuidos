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

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	@Bean
	public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
		return factory -> factory.setPort(portaPadrao);
	}

	public static void fillHash(){
		for(int i = 1; i <= 5000; i++){
			clientes.put(i, new Cliente(i, new BigDecimal("100.00")));
		}
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

			return 200;
		}catch (Exception e) {
			// e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/transferenciaInterna")
	public int transferenciaInterna(@RequestParam(value = "clienteId") int clienteId, @RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "clienteDestino") int clienteDestino){
		try {
			// teste req: http://localhost:8080/transferenciaInterna?clienteId=11&valor=12&clienteDestino=21
			Cliente clienteDestinoPix = clientes.get(clienteDestino);
			Cliente clienteOrigem = clientes.get(clienteId);

			if(clienteOrigem.saldo.compareTo(valor) < 0){
				return 405;
			}

			clienteOrigem.descontarSaldo(valor);
			clienteDestinoPix.adicionarSaldo(valor);

			return 200;
		} catch (Exception e) {
			// e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/transferenciaExterna")
	public int transferenciaExterna(@RequestParam(value = "clienteId") int clienteId, @RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "chaveDestino") String chaveDestino){
		if(!clientes.containsKey(clienteId)){
			return 404;
		}

		Cliente clienteOrigem = clientes.get(clienteId);
		BigDecimal saldoPrevio = clienteOrigem.saldo;

		try {
			// teste req: http://localhost:8080/transferenciaExterna?clienteId=11&valor=12&chaveDestino=a1
			ClienteDestino clienteDestinoPix = obterClienteDestino(chaveDestino);

			if(clienteOrigem.saldo.compareTo(valor) < 0){
				return 405;
			}

			if(chamarApiRecebimento(valor, clienteDestinoPix.numeroConta, clienteDestinoPix.nomeInstituicao) == 200){
				clienteOrigem.descontarSaldo(valor);
				return 200;
			}else{
				clienteOrigem.resetarSaldo(saldoPrevio);
				return 406;
			}
		} catch (Exception e) {
			clienteOrigem.resetarSaldo(saldoPrevio);
			return 404;
		}
	}

	private static ClienteDestino obterClienteDestino(String chaveDestino) {
		String apiUrl = "http://10.210.23.228:5288/Bacen?chave=" + chaveDestino;
		// String apiUrl = "https://localhost:7048/Bacen?chave=" + chaveDestino;


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

				String instituicaoNome = jsonResponse.getAsJsonObject("clienteData").get("nomeInstituicao").getAsString();
				int clienteDestinoId = jsonResponse.getAsJsonObject("clienteData").get("numeroConta").getAsInt();

				ClienteDestino clienteDestinoPix = new ClienteDestino();
				clienteDestinoPix.nomeInstituicao = instituicaoNome;
				clienteDestinoPix.numeroConta = clienteDestinoId;

				return clienteDestinoPix;
			} else {
				return null;
			}
		} catch (IOException e) {
			// e.printStackTrace();
			return null;
		}
	}

	private static int chamarApiRecebimento(BigDecimal valor, int destinoId, String bancoDestino) {
		String apiUrl = "http://localhost:" + direcionamentoPortas(bancoDestino) + "/receberValor?valor=" + valor + "&destinoId=" + destinoId;

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
				return 404;
			}
		} catch (IOException e) {
			// e.printStackTrace();
			return 404;
		}
	}

	@GetMapping("/receberValor")
	public int receberValor(@RequestParam(value = "valor") BigDecimal valor, @RequestParam(value = "destinoId") int destinoId){
		if(!clientes.containsKey(destinoId)){
			return 404;
		}

		Cliente clienteDestinoPix = clientes.get(destinoId);
		BigDecimal saldoPrevio = clienteDestinoPix.saldo;

		try {
			// teste req: http://localhost:8080/receberValor?valor=12&destinoId=11
			clienteDestinoPix.adicionarSaldo(valor);

			return 200;
		} catch (Exception e) {
			// e.printStackTrace();
			clienteDestinoPix.resetarSaldo(saldoPrevio);
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

	@GetMapping("/teste")
	public String testeCreate(@RequestParam(value = "key", defaultValue = "a1") String chave, @RequestParam(value = "k2", defaultValue = "a2") String c2){
		return String.format("Response: %s - %s", chave, c2);
		// teste req: http://localhost:8080/teste?key=a55&k2=a44
	}

	@GetMapping("/testePath/{id}/{id2}")
	public String testePath(@PathVariable Integer id, @PathVariable Integer id2){
		return String.format("Response: %d - %d", id, id2);
		// teste req: http://localhost:8080/teste?key=a55&k2=a44
	}
}

// class Transacao {
//     private List<Operacao> operacoes = new ArrayList<>();

//     public void adicionarOperacao(Operacao operacao) {
//         operacoes.add(operacao);
//     }

//     public void iniciarTransacao() {
//         // Lógica para iniciar a transação (se necessário)
//     }

//     public void reverterTransacao() {
//         for (Operacao operacao : operacoes) {
//             operacao.reverter();
//         }
//     }

//     public void completarTransacao() {
//         for (Operacao operacao : operacoes) {
//             operacao.aplicar();
//         }
//     }
// }

class Cliente{
	int clienteId;
	BigDecimal saldo;
	private Lock lock = new ReentrantLock();

	public Cliente(int clienteId, BigDecimal saldo) {
		this.clienteId = clienteId;
		this.saldo = saldo;
	}

	public void resetarSaldo(BigDecimal valor) {
		lock.lock();
		try{
			this.saldo = valor;
		}finally{
			lock.unlock();
		}
	}

	public void descontarSaldo(BigDecimal valor) {
		lock.lock();
		try{
			if(this.saldo.compareTo(valor) >= 0){
				this.saldo = this.saldo.subtract(valor);
			}	
		}finally{
			lock.unlock();
		}
	}

	public void adicionarSaldo(BigDecimal valor) {
		lock.lock();
		try{
			this.saldo = this.saldo.add(valor);
		}finally{
			lock.unlock();
		}
	}

	// @Override
    // public synchronized void aplicar() {
    //     // Lógica para aplicar a operação no saldo do cliente
    // }

    // @Override
    // public synchronized void reverter() {
    //     // Lógica para reverter a operação no saldo do cliente
    // }
}


class ClienteDestino{
	String nomeInstituicao;
	int numeroConta;
}