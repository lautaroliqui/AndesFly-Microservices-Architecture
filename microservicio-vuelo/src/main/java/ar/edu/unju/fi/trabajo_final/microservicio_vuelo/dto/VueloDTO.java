package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VueloDTO {
    private Long id;
    private String codigo;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private int cupoTotal;
    private int cupoReservado;
}
