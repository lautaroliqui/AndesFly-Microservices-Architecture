package ar.edu.unju.fi.trabajo_final.microservicio_reserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class MicroservicioReservaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioReservaApplication.class, args);
	}

}
