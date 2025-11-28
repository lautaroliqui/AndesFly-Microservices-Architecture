package ar.edu.unju.fi.trabajo_final.microservicio_cliente.dto;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Domicilio;
import lombok.Data;
import java.util.List;

@Data
public class ClienteDTO {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String documento;

    private List<Domicilio> domicilios;
}