package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "destinos")
public class Destino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String nombre;

    @NotBlank
    @Column(length = 20, nullable = false,unique = true)
    private String codigo;

    @NotBlank
    @Column(length = 30,nullable = false)
    private String pais;
}
