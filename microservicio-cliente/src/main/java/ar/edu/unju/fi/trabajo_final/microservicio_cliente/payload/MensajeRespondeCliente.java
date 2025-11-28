package ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.dto.ClienteDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
public class MensajeRespondeCliente {
    private String mensaje;
    private ClienteDTO cliente;
    private List<ClienteDTO> clienteLista;
}
