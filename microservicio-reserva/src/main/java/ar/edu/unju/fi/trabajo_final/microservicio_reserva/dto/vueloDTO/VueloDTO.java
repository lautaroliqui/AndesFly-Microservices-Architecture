package ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.vueloDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
