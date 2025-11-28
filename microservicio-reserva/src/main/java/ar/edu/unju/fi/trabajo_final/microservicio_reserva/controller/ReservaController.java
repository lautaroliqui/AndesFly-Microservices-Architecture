package ar.edu.unju.fi.trabajo_final.microservicio_reserva.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.configuration.ReservaServiceConfiguration;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.PropertiesReserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeReserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.ReservaService; // <-- Importa la Interfaz, no la Impl
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DTO simple para la petición de crear una reserva como usuario.
 * No contiene clienteId, ya que se extrae del token.
 */
record ReservaRequestDTO(Long vueloId, String codigo, String observaciones) {}

@RestController
@RequestMapping("api/v1_1/")
@Tag(name = "Reservas", description = "Controlador para Reservas")
public class ReservaController implements ApiReserva {

    private final ReservaService reservaService;
    private final ReservaServiceConfiguration configReserva;

    public ReservaController(ReservaService reservaService, ReservaServiceConfiguration configReserva) {
        this.reservaService = reservaService;
        this.configReserva = configReserva;
    }

    // ---
    // ENDPOINTS PARA ROLE_USER (CLIENTE AUTENTICADO)
    // ---

    @Override
    public ResponseEntity<MensajeRespondeReserva> obtenerMisReservas(@AuthenticationPrincipal Jwt jwt) {
        // --- ¡CORRECCIÓN AQUÍ! ---
        Long clienteId = (Long) jwt.getClaim("id");

        List<Reserva> reservas = reservaService.buscarReservasPorCliente(clienteId);

        MensajeRespondeReserva responde = MensajeRespondeReserva.builder()
                .mensaje("Reservas del usuario encontradas")
                .reservaLista(reservaService.clienteVueloEsamblador(reservas)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(responde, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeReserva> crearMiReserva(
            @RequestBody ReservaRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        // --- ¡CORRECCIÓN AQUÍ! ---
        Long clienteId = (Long) jwt.getClaim("id");

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setVueloId(request.vueloId());
        nuevaReserva.setObservaciones(request.observaciones());
        nuevaReserva.setCodigo(request.codigo());

        Reserva reservaCreada = reservaService.crearReservaParaUsuario(nuevaReserva, clienteId);

        MensajeRespondeReserva mensaje = MensajeRespondeReserva.builder()
                .mensaje("Reserva creada exitosamente para el usuario")
                .reserva(reservaService.clienteVueloEsamblador(reservaCreada)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }


    // ---
    // ENDPOINTS DE ADMINISTRADOR (LOS ANTIGUOS)
    // ---

    @Override
    public ResponseEntity<MensajeRespondeReserva> crear(@RequestBody Reserva reserva){
        Reserva reservaCreada = reservaService.crear(reserva);
        MensajeRespondeReserva mensaje = MensajeRespondeReserva.builder()
                .mensaje("Reserva (Admin) creada exitosamente")
                .reserva(reservaService.clienteVueloEsamblador(reservaCreada)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MensajeRespondeReserva> obtenerReservas(){
        List<Reserva> reservas = reservaService.buscarTodasReserva();
        MensajeRespondeReserva responde = MensajeRespondeReserva.builder()
                .mensaje("Reservas encontradas")
                .reservaLista(reservaService.clienteVueloEsamblador(reservas)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(responde, HttpStatus.OK);
    }

    // ---
    // ENDPOINTS COMUNES (Confirmar/Cancelar/Ver ID)
    // ---

    @Override
    public ResponseEntity<MensajeRespondeReserva> confirmarReserva(@PathVariable Long id){
        Reserva reservaConfirmada = reservaService.confirmar(id);
        MensajeRespondeReserva mensaje = MensajeRespondeReserva.builder()
                .mensaje("Reserva Confirmada")
                .reserva(reservaService.clienteVueloEsamblador(reservaConfirmada)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(mensaje, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeReserva> cancelarReserva(@PathVariable Long id){
        Reserva reservaCancelada = reservaService.cancelar(id);
        MensajeRespondeReserva mensaje = MensajeRespondeReserva.builder()
                .mensaje("Reserva Cancelada")
                .reserva(reservaService.clienteVueloEsamblador(reservaCancelada)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(mensaje, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MensajeRespondeReserva> obtenerReservasID(@PathVariable Long id){
        Reserva reserva = reservaService.buscarReservaId(id);
        MensajeRespondeReserva responde = MensajeRespondeReserva.builder()
                .mensaje("Reserva encontrada")
                .reserva(reservaService.clienteVueloEsamblador(reserva)) // <-- Ahora esto compila
                .build();
        return new ResponseEntity<>(responde, HttpStatus.OK);
    }

    @Override
    public String obtenerPropiedades()throws JsonProcessingException {
        ObjectWriter owj = new ObjectMapper().writer().withDefaultPrettyPrinter();
        PropertiesReserva propHotels = new PropertiesReserva(configReserva.getMsg(),
                configReserva.getBuildVersion(),configReserva.getMailDetails());
        String jsonString = owj.writeValueAsString(propHotels);
        return jsonString;
    }
}