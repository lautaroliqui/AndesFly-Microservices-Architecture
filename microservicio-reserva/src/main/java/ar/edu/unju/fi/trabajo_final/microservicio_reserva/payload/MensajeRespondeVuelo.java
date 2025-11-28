package ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.vueloDTO.VueloDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.VueloRemote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeRespondeVuelo {
    private String mensaje;
    private VueloRemote vuelo;
    private List<VueloRemote> vueloLista;

}