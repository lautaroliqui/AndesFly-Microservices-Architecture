package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    Optional<Vuelo> findById(Long id);
    Optional<Vuelo> findByCodigo(String codigo);
    List<Vuelo> findByDestino_Id(Long idDestinoDestino);
    List<Vuelo> findByOrigen_Id(Long idDestinoOrigen);
    List<Vuelo> findByFechaSalida(LocalDateTime fecha);
    List<Vuelo> findByFechaSalidaBetween(LocalDateTime fechaMin, LocalDateTime fechaMax);
    List<Vuelo> findByOrigenAndDestinoAndFechaSalidaBetween(
            Destino origen,
            Destino destino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}
