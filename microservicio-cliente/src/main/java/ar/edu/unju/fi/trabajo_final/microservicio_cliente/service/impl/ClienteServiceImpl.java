package ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Domicilio;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.repository.ClienteRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de las operaciones de negocio para la entidad Cliente.
 * Incluye la lógica de validación de unicidad de email y documento.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImpl.class);
    private final ClienteRepository clienteRepository;

    /**
     * Inyección de dependencias por constructor (Good Clean Code).
     * @param clienteRepository El repositorio JPA para Cliente.
     */
    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // --- Métodos de Ayuda para Validación ---

    /**
     * Válida que el email y documento sean únicos, excepto si pertenecen al cliente con 'idExcluir'.
     *
     * @param email     Email a validar.
     * @param documento Documento a validar.
     * @throws ValidacionException Sí encuentra duplicados.
     */
    private void validarUnicidad(String email, String documento, Long idExcluir) throws ValidacionException {
        // (No permitimos cambiar el email en esta lógica,
        // pero la validación de documento sigue siendo crucial).

        // Validación de Email Duplicado (si se proporciona)
        if (email != null) {
            Cliente existentePorEmail = clienteRepository.findByEmail(email);
            if (existentePorEmail != null && (idExcluir == null || !existentePorEmail.getId().equals(idExcluir))) {
                logger.warn("VALIDACIÓN FALLIDA: El email '{}' ya pertenece al Cliente ID {}.", email, existentePorEmail.getId());
                throw new ValidacionException("El email ya pertenece a otro cliente.");
            }
        }

        // Validación de Documento Duplicado (si se proporciona)
        if (documento != null) {
            Cliente existentePorDoc = clienteRepository.findByDocumento(documento);
            if (existentePorDoc != null && (idExcluir == null || !existentePorDoc.getId().equals(idExcluir))) {
                logger.warn("VALIDACIÓN FALLIDA: El documento '{}' ya pertenece al Cliente ID {}.", documento, existentePorDoc.getId());
                throw new ValidacionException("El documento ya pertenece a otro cliente.");
            }
        }
    }

    // --- Implementación del Contrato ClienteService ---

    @Override
    @Transactional
    public Cliente guardar(Cliente cliente) throws ValidacionException {
        // (Lógica para 'register' y 'admin' crear)
        validarUnicidad(cliente.getEmail(), cliente.getDocumento(), null);

        if (cliente.getDomicilios() != null && !cliente.getDomicilios().isEmpty()) {
            for (Domicilio domicilio : cliente.getDomicilios()) {
                domicilio.setCliente(cliente);
            }
        }

        Cliente nuevoCliente = clienteRepository.save(cliente);
        logger.info("Cliente guardado exitosamente. ID: {}", nuevoCliente.getId());
        return nuevoCliente;
    }

    @Override
    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) throws ElementoNoEncontradoException {
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("BÚSQUEDA FALLIDA: Cliente con ID {} no encontrado.", id);
                    return new ElementoNoEncontradoException("Cliente con ID " + id + " no encontrado.");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorEmail(String email) throws ElementoNoEncontradoException {
        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente == null) {
            logger.warn("BÚSQUEDA FALLIDA: Cliente con email {} no encontrado.", email);
            throw new ElementoNoEncontradoException("Cliente con email " + email + " no encontrado.");
        }

        return cliente;
    }

    @Override
    @Transactional
    public Cliente actualizar(Cliente cliente) throws ElementoNoEncontradoException, ValidacionException {
        // (Este es el método de ADMIN)
        Cliente clienteExistente = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> {
                    logger.info("ACTUALIZAR FALLIDO: Cliente con ID {} no encontrado para actualizar.", cliente.getId());
                    return new ElementoNoEncontradoException("Cliente con ID " + cliente.getId() + " no encontrado.");
                });

        validarUnicidad(cliente.getEmail(), cliente.getDocumento(), cliente.getId());

        // El Admin PUEDE cambiar el email de otro
        clienteExistente.setNombreCompleto(cliente.getNombreCompleto());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setDocumento(cliente.getDocumento());

        // (Manejo de domicilios)
        clienteExistente.getDomicilios().clear();
        if (cliente.getDomicilios() != null && !cliente.getDomicilios().isEmpty()) {
            for (Domicilio domicilioNuevo : cliente.getDomicilios()) {
                domicilioNuevo.setCliente(clienteExistente);
                clienteExistente.getDomicilios().add(domicilioNuevo);
            }
        }

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        logger.info("Cliente ID {} (Admin) actualizado exitosamente.", cliente.getId());
        return clienteActualizado;
    }

    @Override
    @Transactional
    public void eliminar(Long id) throws ElementoNoEncontradoException {
        if (!clienteRepository.existsById(id)) {
            logger.info("ELIMINAR FALLIDO: Cliente con ID {} no encontrado para eliminar.", id);
            throw new ElementoNoEncontradoException("Cliente con ID " + id + " no encontrado.");
        }

        clienteRepository.deleteById(id);
        logger.info("Cliente ID {} eliminado exitosamente.", id);
    }


    // --- IMPLEMENTACIÓN DE MÉTODOS "MI PERFIL" ---

    @Override
    @Transactional(readOnly = true)
    public Cliente obtenerMiPerfil(Long clienteId) throws ElementoNoEncontradoException {
        // Simplemente busca al cliente por el ID (que vino del token)
        return this.buscarPorId(clienteId);
    }

    @Override
    @Transactional
    public Cliente actualizarMiPerfil(Cliente clienteNuevosDatos, Long clienteId)
            throws ElementoNoEncontradoException, ValidacionException {

        // 1. Obtiene el perfil actual de la BD usando el ID del TOKEN (seguro)
        Cliente perfilActual = this.buscarPorId(clienteId);

        // 2. Valida solo el documento (el email NO se puede cambiar desde el perfil)
        validarUnicidad(null, clienteNuevosDatos.getDocumento(), clienteId);

        // 3. Actualiza solo los campos permitidos
        perfilActual.setNombreCompleto(clienteNuevosDatos.getNombreCompleto());
        perfilActual.setDocumento(clienteNuevosDatos.getDocumento());

        // 4. Actualiza los domicilios (misma lógica que 'actualizar')
        perfilActual.getDomicilios().clear();
        if (clienteNuevosDatos.getDomicilios() != null && !clienteNuevosDatos.getDomicilios().isEmpty()) {
            for (Domicilio domicilioNuevo : clienteNuevosDatos.getDomicilios()) {
                domicilioNuevo.setCliente(perfilActual);
                perfilActual.getDomicilios().add(domicilioNuevo);
            }
        }

        Cliente perfilActualizado = clienteRepository.save(perfilActual);
        logger.info("Perfil del Cliente ID {} actualizado exitosamente.", clienteId);
        return perfilActualizado;
    }
}