package ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.clienteDTO.ClienteDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.ClienteRemote;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeRespondeCliente {
    private String mensaje;
    private ClienteRemote cliente;
    private List<ClienteRemote> clienteLista;
}