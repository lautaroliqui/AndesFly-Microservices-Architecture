package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto.DestinoDTO;
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
public class MensajeRespondeDestino {
    private String mensaje;
    private DestinoDTO destino;
    private List<DestinoDTO> destinoLista;


}
