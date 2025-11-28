package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que define las operaciones principales relacionadas con la gestion de vuelos.
 * Provee metodos para crear, actualizar, eliminar y consultar vuelos segun distintos criterios.
 */
public interface VueloService {

    /**
     * Crea un nuevo vuelo despues de validar la unicidad del codigo y la coherencia de los datos.
     *
     * @param unVuelo El objeto {@link Vuelo} a crear.
     * @return El vuelo creado (persistido en la base de datos).
     * @throws ElementoExistenteExcepction Si ya existe un vuelo con el mismo codigo.
     * @throws ElementoNoEncontradoException Si el origen o destino no existen en la base de datos.
     * @throws ValidacionException Si los datos del vuelo son invalidos (por ejemplo,
     *                             cupo reservado mayor al total o fechas incoherentes).
     */
    Vuelo crear(Vuelo unVuelo)
            throws ElementoExistenteExcepction, ElementoNoEncontradoException, ValidacionException;

    /**
     * Actualiza la informacion de un vuelo existente validando que el codigo no este duplicado
     * y que los datos sean coherentes.
     *
     * @param unVuelo El objeto {@link Vuelo} con la informacion actualizada.
     * @return El vuelo actualizado.
     * @throws ElementoNoEncontradoException Si el vuelo, el origen o el destino no existen.
     * @throws ElementoExistenteExcepction Si ya existe otro vuelo con el mismo codigo.
     * @throws ValidacionException Si los datos del vuelo son invalidos.
     */
    Vuelo actualizar(Vuelo unVuelo)
            throws ElementoNoEncontradoException, ElementoExistenteExcepction, ValidacionException;

    /**
     * Elimina un vuelo existente segun su ID.
     *
     * @param id El identificador unico del vuelo.
     * @throws ElementoNoEncontradoException Si no se encuentra un vuelo con el ID indicado.
     */
    void eliminar(Long id) throws ElementoNoEncontradoException;

    /**
     * Obtiene todos los vuelos registrados en el sistema.
     *
     * @return Una lista de todos los vuelos existentes.
     */
    List<Vuelo> obtenerTodosVuelos();

    /**
     * Busca un vuelo especifico por su identificador unico.
     *
     * @param id El ID del vuelo a buscar.
     * @return El vuelo correspondiente al ID indicado.
     * @throws ElementoNoEncontradoException Si no existe un vuelo con el ID indicado.
     */
    Vuelo obtenerPorId(Long id) throws ElementoNoEncontradoException;

    /**
     * Obtiene todos los vuelos cuyo origen coincida con el ID indicado.
     *
     * @param id El ID del origen de los vuelos a buscar.
     * @return Una lista de vuelos que tienen el origen indicado.
     * @throws ElementoNoEncontradoException Si no existen vuelos con el origen indicado.
     */
    List<Vuelo> obtenerOrigen(Long id) throws ElementoNoEncontradoException;

    /**
     * Obtiene todos los vuelos cuyo destino coincida con el ID indicado.
     *
     * @param id El ID del destino de los vuelos a buscar.
     * @return Una lista de vuelos que tienen el destino indicado.
     * @throws ElementoNoEncontradoException Si no existen vuelos con el destino indicado.
     */
    List<Vuelo> obtenerDestino(Long id) throws ElementoNoEncontradoException;

    /**
     * Busca vuelos que tengan una fecha de salida exacta.
     *
     * @param fechaExacta La fecha y hora exacta de salida.
     * @return Una lista de vuelos con la fecha de salida igual a la indicada.
     */
    List<Vuelo> buscarPorFechaDeSalida(LocalDateTime fechaExacta);

    /**
     * Busca vuelos cuya fecha de salida se encuentre dentro de un rango determinado.
     *
     * @param fechaMin La fecha minima de salida.
     * @param fechaMax La fecha maxima de salida.
     * @return Una lista de vuelos con fechas de salida dentro del rango indicado.
     * @throws ValidacionException Si el rango de fechas es invalido (por ejemplo, si {@code fechaMin} es posterior a {@code fechaMax}).
     */
    List<Vuelo> buscarPorFechaDeSalida(LocalDateTime fechaMin, LocalDateTime fechaMax)
            throws ValidacionException;

    /**
     * Confirma la reserva de un cupo en un vuelo específico.
     * Incrementa 'cupoReservado' en 1.
     *
     * @param idVuelo El ID del vuelo a confirmar.
     * @return El vuelo actualizado con el cupo reservado.
     * @throws ElementoNoEncontradoException Si el vuelo no existe.
     * @throws ValidacionException Si el vuelo ya está lleno (cupo reservado >= cupo total).
     */
    Vuelo confirmarReserva(Long idVuelo)
            throws ElementoNoEncontradoException, ValidacionException;

    /**
     * Cancela la reserva de un cupo en un vuelo específico.
     * Decrementa 'cupoReservado' en 1 (si es mayor a 0).
     *
     * @param idVuelo El ID del vuelo a cancelar.
     * @return El vuelo actualizado con el cupo liberado.
     * @throws ElementoNoEncontradoException Si el vuelo no existe.
     */
    Vuelo cancelarReserva(Long idVuelo)
            throws ElementoNoEncontradoException;

    /**
     * Busca vuelos basados en códigos de origen, destino y una fecha específica.
     *
     * @param origenCodigo Código del aeropuerto de origen (ej. "JUJ").
     * @param destinoCodigo Código del aeropuerto de destino (ej. "AEP").
     * @param fecha La fecha de salida (se buscará en un rango de 24h).
     * @return Una lista de vuelos que coinciden.
     */
    List<Vuelo> buscarVuelosPor(String origenCodigo, String destinoCodigo, LocalDate fecha);
}
