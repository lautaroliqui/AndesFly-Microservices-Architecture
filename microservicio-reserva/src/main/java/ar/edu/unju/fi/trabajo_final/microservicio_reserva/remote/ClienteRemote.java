package ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRemote {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String documento;
}
