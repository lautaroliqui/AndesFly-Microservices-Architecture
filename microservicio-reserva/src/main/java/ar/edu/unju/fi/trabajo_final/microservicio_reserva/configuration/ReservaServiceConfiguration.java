package ar.edu.unju.fi.trabajo_final.microservicio_reserva.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "reserva")
@Data
public class ReservaServiceConfiguration {
    private String msg;
    private String buildVersion;
    private Map<String,String> mailDetails;
}

