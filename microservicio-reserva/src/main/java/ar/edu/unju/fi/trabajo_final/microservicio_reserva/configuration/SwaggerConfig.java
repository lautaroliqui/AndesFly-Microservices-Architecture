package ar.edu.unju.fi.trabajo_final.microservicio_reserva.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =  @Info(
                title = "API de Reserva",
                description = "API RESERVA se encarga de gestionar las reservas de la Agencia de viaje AndesFly",
                version = "1.0.0",
                contact = @Contact(
                        name = "Grupo04-DAAS",
                        email = "grupo04DAAS@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "DEV-SERVER",
                        url = "http://localhost:8082"
                ),
                @Server(
                        description = "PROD-SERVER",
                        url = "http://localhost:9000"
                )
        }
)
public class SwaggerConfig {}
