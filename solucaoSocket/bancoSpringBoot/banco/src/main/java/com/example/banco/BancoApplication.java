package com.example.banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BancoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BancoApplication.class, args);
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
