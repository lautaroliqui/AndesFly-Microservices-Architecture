package ar.edu.unju.fi.trabajo_final.microservicio_vuelo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioVueloApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioVueloApplication.class, args);
	}

}
