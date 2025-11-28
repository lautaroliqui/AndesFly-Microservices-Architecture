package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino,Long> {
    Optional<Destino> findById(Long id);
    Optional<List<Destino>> findByNombre(String nombre);
    Optional<Destino> findByCodigo(String codigo);
    Optional<List<Destino>> findByPais(String pais);
}
