package ar.edu.unju.fi.trabajo_final.microservicio_cliente.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.config.ClienteServiceConfiguration;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.dto.ClienteMapper;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.PropertiesCliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload.MensajeRespondeCliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// --- ¡AÑADIR IMPORTS DE SEGURIDAD! ---
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1_1/")
@Tag(name = "Clientes", description = "Endpoints para Gestión de Clientes (ADMIN) y Perfil (USER)")
public class ClienteController  implements ApiCliente{

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;
    private final ClienteServiceConfiguration configCliente;

    // Inyección por Constructor (mejor práctica)
    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper, ClienteServiceConfiguration configCliente) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
        this.configCliente = configCliente;
    }

    // ---
    // ENDPOINTS DE "MI PERFIL" (PARA ROLE_USER)
    // ---

    @Override
    public ResponseEntity<MensajeRespondeCliente> obtenerMiPerfil(@AuthenticationPrincipal Jwt jwt) {
        // Extrae el ID del usuario del token (seguro)
        Long clienteId = (Long) jwt.getClaim("id");
        Cliente cliente = clienteService.obtenerMiPerfil(clienteId);

        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Perfil obtenido exitosamente")
                .cliente(clienteMapper.clienteToClienteDTO(cliente))
                .build(),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeCliente> actualizarMiPerfil(
            @Valid @RequestBody Cliente clienteNuevosDatos,
            @AuthenticationPrincipal Jwt jwt) {

        // Extrae el ID del usuario del token (seguro)
        Long clienteId = (Long) jwt.getClaim("id");
        Cliente clienteActualizado = clienteService.actualizarMiPerfil(clienteNuevosDatos, clienteId);

        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Perfil actualizado exitosamente.")
                .cliente(clienteMapper.clienteToClienteDTO(clienteActualizado))
                .build(),
                HttpStatus.OK);
    }

    // ---
    // ENDPOINTS DE GESTIÓN (PARA ROLE_ADMIN)
    // ---

    @Override
    public ResponseEntity<MensajeRespondeCliente> crearCliente(@Valid @RequestBody Cliente cliente){
        Cliente clienteSave = clienteService.guardar(cliente);
        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Cliente guardado exitosamente")
                .cliente(clienteMapper.clienteToClienteDTO(clienteSave))
                .build()
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeCliente> actualizarCliente(@Valid @RequestBody Cliente cliente){
        Cliente clienteUpdate = clienteService.actualizar(cliente);
        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Cliente actualizado exitosamente.")
                .cliente(clienteMapper.clienteToClienteDTO(clienteUpdate))
                .build()
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeCliente> eliminarCliente(@PathVariable Long id){
        clienteService.eliminar(id);
        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Cliente eliminado exitosamente.")
                .cliente(null)
                .build()
                , HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<MensajeRespondeCliente> obtenerClientes(){
        List<Cliente> clientes = clienteService.buscarTodos();

        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Cliente obtenidos exitosamente.")
                .clienteLista(clienteMapper.listaClienteTolistaClienteDTO(clientes))
                .build()
                , HttpStatus.OK);

    }

    @Override
    public ResponseEntity<MensajeRespondeCliente> obtenerClientePorId(@PathVariable Long id){
        Cliente cliente = clienteService.buscarPorId(id);
        return new ResponseEntity<>(MensajeRespondeCliente.builder()
                .mensaje("Cliente encontrado exitosamente.")
                .cliente(clienteMapper.clienteToClienteDTO(cliente))
                .build()
                , HttpStatus.OK);
    }

    @Override
    public String obtenerPropiedades()throws JsonProcessingException {
        ObjectWriter owj = new ObjectMapper().writer().withDefaultPrettyPrinter();
        PropertiesCliente propHotels = new PropertiesCliente(configCliente.getMsg(),
                configCliente.getBuildVersion(),configCliente.getMailDetails());
        String jsonString = owj.writeValueAsString(propHotels);
        return jsonString;
    }
}