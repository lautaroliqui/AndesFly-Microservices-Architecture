package ar.edu.unju.fi.trabajo_final.microservicio_cliente.service;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ValidacionException;
import java.util.List;

/**
 * Define el contrato de las operaciones de negocio para la entidad Cliente (CRUD y validaciones).
 * Esta interfaz es utilizada por la capa de controladores.
 */
public interface ClienteService {

    /**
     * Guarda un nuevo Cliente después de validar la unicidad del email y documento.
     * (Usado por /auth/register y por ADMIN /cliente)
     * @param cliente El objeto Cliente a guardar.
     * @return El Cliente guardado (con su ID).
     * @throws ValidacionException Si el email o el documento ya existen.
     */
    Cliente guardar(Cliente cliente) throws ValidacionException;

    /**
     * [ADMIN] Busca y retorna todos los Clientes.
     * @return Una lista de todos los Clientes.
     */
    List<Cliente> buscarTodos();

    /**
     * [ADMIN/INTERNO] Busca un Cliente por su ID.
     * @param id El ID del cliente a buscar.
     * @return El Cliente encontrado.
     * @throws ElementoNoEncontradoException Si el Cliente no existe.
     */
    Cliente buscarPorId(Long id) throws ElementoNoEncontradoException;

    /**
     * [ADMIN] Actualiza la información de un Cliente existente (vía ID del body).
     * @param cliente El Cliente con la información actualizada.
     * @return El Cliente actualizado.
     * @throws ElementoNoEncontradoException Si el Cliente no existe.
     * @throws ValidacionException Si los datos de actualización violan la unicidad de email/documento.
     */
    Cliente actualizar(Cliente cliente) throws ElementoNoEncontradoException, ValidacionException;

    /**
     * [ADMIN] Elimina un Cliente por su ID.
     * @param id El ID del cliente a eliminar.
     * @throws ElementoNoEncontradoException Si el Cliente no existe.
     */
    void eliminar(Long id) throws ElementoNoEncontradoException;

    /**
     * [INTERNO] Busca un cliente por su email. Esencial para el proceso de autenticación.
     *
     * @param email El email del usuario a buscar.
     * @return El Cliente correspondiente.
     * @throws ElementoNoEncontradoException Si no se encuentra ningún cliente con ese email.
     */
    Cliente buscarPorEmail(String email) throws ElementoNoEncontradoException;


    // --- NUEVOS MÉTODOS PARA "MI PERFIL" (SPRINT 4) ---

    /**
     * [USER] Obtiene los datos del perfil del usuario actualmente autenticado.
     * @param clienteId El ID del usuario (extraído del token JWT).
     * @return El Cliente (perfil) del usuario.
     * @throws ElementoNoEncontradoException Si el ID del token no corresponde a un cliente.
     */
    Cliente obtenerMiPerfil(Long clienteId) throws ElementoNoEncontradoException;

    /**
     * [USER] Actualiza los datos del perfil (nombre, documento, domicilios)
     * del usuario actualmente autenticado.
     *
     * @param clienteNuevosDatos El objeto Cliente con los nuevos datos (el email no se puede cambiar).
     * @param clienteId El ID del usuario (extraído del token JWT).
     * @return El Cliente actualizado.
     * @throws ElementoNoEncontradoException Si el ID del token no corresponde a un cliente.
     * @throws ValidacionException Si el nuevo documento ya pertenece a otro usuario.
     */
    Cliente actualizarMiPerfil(Cliente clienteNuevosDatos, Long clienteId) throws ElementoNoEncontradoException, ValidacionException;

}