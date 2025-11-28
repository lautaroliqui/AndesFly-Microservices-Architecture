package ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.reservaDTO;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.enums.EstadoReserva;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ReservaDTO {

    private Long id;
    private String codigo;
    private EstadoReserva estado;
    private String cliente;
    private String CodigoVuelo;
    private String origen;
    private String destino;
    private String observaciones;
}
