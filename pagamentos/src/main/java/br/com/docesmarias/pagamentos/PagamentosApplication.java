package br.com.docesmarias.pagamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient                   //indica que é um client da eureka
@EnableFeignClients                   //Comunicação sincrona com openFeign
public class PagamentosApplication {

	public static void main(String[] args) {

		SpringApplication.run(PagamentosApplication.class, args);
	}

}
