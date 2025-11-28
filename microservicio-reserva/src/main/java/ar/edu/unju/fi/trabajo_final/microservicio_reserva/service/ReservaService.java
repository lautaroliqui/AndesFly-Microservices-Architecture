package ar.edu.unju.fi.trabajo_final.microservicio_reserva.service;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.reservaDTO.ReservaDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.*;
import java.util.List;

public interface ReservaService {
    /**
     * Crea una nueva reserva y valida la existencia del vuelo y del cliente.
     * Además verifica que el código de reserva sea único.
     * La reserva se crea con estado {@code GENERADO}.
     *
     * @param reserva La reserva a crear.
     * @return La reserva creada con su ID asignado.
     * @throws ElementoExistenteException Si el código de reserva ya existe.
     * @throws ValidacionException Si falla alguna validación de negocio.
     */
    Reserva crear(Reserva reserva);

    /**
     * Confirma una reserva existente.
     * Solo se puede confirmar si el estado actual es {@code GENERADO}.
     * Actualiza el cupo reservado del vuelo correspondiente.
     *
     * @param id El ID de la reserva a confirmar.
     * @return La reserva confirmada con estado {@code CONFIRMAR}.
     * @throws ElementoNoEncontradoException Si no existe la reserva con el ID dado.
     * @throws ValidacionException Si el estado de la reserva no permite confirmación o si el vuelo está completo.
     */
    Reserva confirmar(Long id);

    /**
     * Cancela una reserva existente.
     * Si la reserva estaba confirmada, se decrementa el cupo reservado del vuelo.
     *
     * @param id El ID de la reserva a cancelar.
     * @return La reserva cancelada con estado {@code CANCELADA}.
     * @throws ElementoNoEncontradoException Si no existe la reserva con el ID dado.
     * @throws ValidacionException Si la reserva ya estaba cancelada.
     */
    Reserva cancelar(Long id);

    /**
     * Recupera todas las reservas existentes (para ADMINS).
     *
     * @return Una lista con todas las reservas.
     */
    List<Reserva> buscarTodasReserva();

    /**
     * Busca una reserva por su ID.
     *
     * @param id El ID de la reserva a buscar.
     * @return La reserva encontrada.
     * @throws ElementoNoEncontradoException Si no existe la reserva con el ID dado.
     */
    Reserva buscarReservaId(Long id);

    /**
     * Busca todas las reservas para un cliente específico.
     * @param clienteId El ID del cliente (obtenido del token JWT).
     * @return Lista de sus reservas.
     */
    List<Reserva> buscarReservasPorCliente(Long clienteId);

    /**
     * Crea una nueva reserva asociada directamente a un usuario (clienteId).
     * El clienteId se obtiene del token, no del body, por seguridad.
     * @param reserva Objeto Reserva (sin clienteId)
     * @param clienteId El ID del usuario autenticado.
     * @return La reserva creada.
     */
    Reserva crearReservaParaUsuario(Reserva reserva, Long clienteId);


    // --- ¡MÉTODOS AÑADIDOS! ---
    /**
     * Ensambla una única Reserva (Entidad) con los datos de Cliente y Vuelo (de Feign)
     * para crear un ReservaDTO (Respuesta).
     *
     * @param reserva La entidad Reserva.
     * @return El ReservaDTO ensamblado.
     */
    ReservaDTO clienteVueloEsamblador(Reserva reserva);

    /**
     * Ensambla una lista de Reservas (Entidad) con los datos de Cliente y Vuelo.
     *
     * @param reservas La lista de entidades Reserva.
     * @return La lista de ReservaDTO ensamblados.
     */
    List<ReservaDTO> clienteVueloEsamblador(List<Reserva> reservas);
}