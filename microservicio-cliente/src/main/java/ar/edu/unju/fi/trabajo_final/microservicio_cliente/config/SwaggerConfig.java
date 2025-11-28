package ar.edu.unju.fi.trabajo_final.microservicio_cliente.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =  @Info(
                title = "API de Cliente",
                description = "API CLIENTE se encarga de gestionar los clientes de la Agencia de viaje AndesFly",
                version = "1.0.0",
                contact = @Contact(
                        name = "Grupo04-DAAS",
                        email = "grupo04DAAS@gmail.com"
                ),
                license = @License(
                        name ="Licencia Apache 2.0",
                        url ="https://springdoc.org"
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
public class SwaggerConfig {
}
