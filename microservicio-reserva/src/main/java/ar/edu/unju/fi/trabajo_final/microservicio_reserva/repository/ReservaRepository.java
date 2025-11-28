package ar.edu.unju.fi.trabajo_final.microservicio_reserva.repository;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
// --- ¡ESTA ES LA IMPORTACIÓN CORRECTA! ---
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByCodigo(String codigo);

    /**
     * Busca todas las reservas asociadas a un ID de cliente específico.
     * @param clienteId El ID del cliente.
     * @param sort El objeto de ordenación (debe ser org.springframework.data.domain.Sort).
     * @return Lista de reservas ordenadas.
     */
    List<Reserva> findByClienteId(Long clienteId, Sort sort);
}