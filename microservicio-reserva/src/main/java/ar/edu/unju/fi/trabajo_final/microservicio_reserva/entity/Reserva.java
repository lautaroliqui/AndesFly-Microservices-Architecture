package ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity;


import ar.edu.unju.fi.trabajo_final.microservicio_reserva.enums.EstadoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table (name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 30, nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoReserva estado;

    @Column(name = "id_cliente",nullable = false)
    private Long clienteId;


    @Column(name = "id_vuelo",nullable = false)
    private Long vueloId;

    @Column(name = "fecha_creacion",nullable = false)
    private LocalDate fechaCreacion;

    @Column
    private String observaciones;

}