package ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.reservaDTO.ReservaDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeRespondeReserva {
    private String mensaje;
    private ReservaDTO reserva;
    private List<ReservaDTO> reservaLista;
}
