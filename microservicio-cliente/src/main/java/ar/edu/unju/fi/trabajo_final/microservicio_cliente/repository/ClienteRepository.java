package ar.edu.unju.fi.trabajo_final.microservicio_cliente.repository;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Cliente.
 * Hereda todas las operaciones CRUD básicas y paginación.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca un Cliente por su dirección de email.
     * Método necesario para la validación de unicidad en la capa de servicio.
     * @param email La dirección de correo electrónico del cliente.
     * @return Cliente o null si no existe.
     */
    Cliente findByEmail(String email);

    /**
     * Busca un Cliente por su número de documento.
     * Método necesario para la validación de unicidad en la capa de servicio.
     * @param documento El número de documento del cliente.
     * @return Cliente o null si no existe.
     */
    Cliente findByDocumento(String documento);
}