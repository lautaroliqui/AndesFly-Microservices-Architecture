package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto.VueloDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
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
    private VueloDTO vuelo;
    private List<VueloDTO> vueloLista;


}
