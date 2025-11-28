package ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class PropertiesReserva {
    private String msg;
    private String buildVersion;
    private Map<String, String> mailDetail;
}
