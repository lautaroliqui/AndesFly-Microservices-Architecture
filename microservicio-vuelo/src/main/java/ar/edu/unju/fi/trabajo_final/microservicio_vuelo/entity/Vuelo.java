package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vuelos")
public class Vuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true,  nullable = false)
    private String codigo;


    @ManyToOne(optional = false)
    @JoinColumn(name = "origen_id")
    private Destino origen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_id")
    private Destino destino;

    @FutureOrPresent
    @Column(nullable = false)
    private LocalDateTime fechaSalida;

    @Future
    @Column(nullable = false)
    private LocalDateTime fechaLlegada;

    @Positive
    private int cupoTotal;

    @PositiveOrZero
    private int cupoReservado;
}
