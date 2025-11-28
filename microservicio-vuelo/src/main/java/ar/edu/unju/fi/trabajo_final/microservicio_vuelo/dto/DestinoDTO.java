package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DestinoDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private String pais;
}
