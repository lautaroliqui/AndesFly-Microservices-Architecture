package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoExistenteExcepction;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoNoEncontradoException;

import java.util.List;

public interface DestinoService {
    /**
     * Crea un nuevo destino validando que el codigo no este repetido.
     *
     * @param destino El objeto {@link Destino} a crear.
     * @return El destino creado (persistido en la base de datos).
     * @throws ElementoExistenteExcepction Si ya existe un destino con el mismo codigo.
     */
    Destino crear(Destino destino) throws ElementoExistenteExcepction;

    /**
     * Obtiene una lista con todos los destinos registrados en el sistema.
     *
     * @return Una lista de todos los destinos.
     */
    List<Destino> listarTodos();

    /**
     * Busca un destino por su identificador unico.
     *
     * @param id El ID del destino a buscar.
     * @return El destino correspondiente al ID indicado.
     * @throws ElementoNoEncontradoException Si no existe un destino con el ID indicado.
     */
    Destino buscarPorID(long id) throws ElementoNoEncontradoException;

    /**
     * Busca un destino por su codigo.
     *
     * @param codigo El codigo del destino a buscar.
     * @return El destino correspondiente al codigo indicado.
     * @throws ElementoNoEncontradoException Si no existe un destino con el codigo indicado.
     */
    Destino buscarPorCodigo(String codigo) throws ElementoNoEncontradoException;

    /**
     * Busca destinos por su nombre.
     *
     * @param nombre El nombre del destino a buscar.
     * @return Una lista de destinos cuyo nombre coincide con el indicado.
     * @throws ElementoNoEncontradoException Si no existen destinos con el nombre indicado.
     */
    List<Destino> buscarPorNombre(String nombre) throws ElementoNoEncontradoException;

    /**
     * Busca destinos por su pais.
     *
     * @param pais El pais de los destinos a buscar.
     * @return Una lista de destinos cuyo pais coincide con el indicado.
     * @throws ElementoNoEncontradoException Si no existen destinos con el pais indicado.
     */
    List<Destino> buscarPorPais(String pais) throws ElementoNoEncontradoException;

    /**
     * Actualiza la informacion de un destino existente.
     *
     * @param destino El objeto {@link Destino} con la informacion actualizada.
     * @return El destino actualizado.
     * @throws ElementoNoEncontradoException Si no existe un destino con el ID indicado.
     * @throws ElementoExistenteExcepction Si ya existe otro destino con el mismo codigo.
     */
    Destino actualizar(Destino destino) throws ElementoNoEncontradoException, ElementoExistenteExcepction;

    /**
     * Elimina un destino segun su identificador.
     *
     * @param id El ID del destino a eliminar.
     * @throws ElementoNoEncontradoException Si no existe un destino con el ID indicado.
     */
    void eliminar(Long id) throws ElementoNoEncontradoException;
}
