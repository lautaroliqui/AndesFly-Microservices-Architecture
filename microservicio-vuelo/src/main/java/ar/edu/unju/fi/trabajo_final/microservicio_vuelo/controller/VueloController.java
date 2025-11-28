package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.config.VueloServiceConfiguration;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto.VueloMapper;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.PropiertiesVuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.MensajeRespondeVuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl.VueloServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("api/v1_1/")
@Tag(name = "Catalogo de Vuelo", description = "Controlador para Vuelo")
public class VueloController implements ApiVuelo {
    private final VueloServiceImpl vueloService;
    @Autowired
    private VueloServiceConfiguration configVuelos;
    @Autowired
    private VueloMapper vueloMapper;

    public VueloController(VueloServiceImpl vueloService) {this.vueloService = vueloService;}

    @Override
    public ResponseEntity<?> crearVuelo(@RequestBody Vuelo vuelo) {
        Vuelo vueloCreado =  vueloService.crear(vuelo);
        return  new ResponseEntity<>(MensajeRespondeVuelo.builder()
                .mensaje("Vuelo creado existosamente.").
                vuelo(vueloMapper.vueloToVueloDTO(vueloCreado)).
                build()
                , HttpStatus.CREATED);

    }

    //       ACTUALIZAR

    @Override
    public ResponseEntity<?> actualizarVuelo(@RequestBody Vuelo vuelo) {
        Vuelo vueloActualizado =  vueloService.actualizar(vuelo);
        return  new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                    .mensaje("Vuelo actualizado existosamente.").
                    vuelo(vueloMapper.vueloToVueloDTO(vueloActualizado)).
                    build()
                ,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> eliminarVuelo(@PathVariable Long id) {
        vueloService.eliminar(id);
        return new  ResponseEntity<>(MensajeRespondeVuelo.builder()
                .mensaje("Vuelo eliminado existosamente.").
                vuelo(null).
                build(),HttpStatus.NO_CONTENT);
    }
    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerVuelos() {
        List<Vuelo> vuelos= vueloService.obtenerTodosVuelos();
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrados existosamente")
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),HttpStatus.OK
        );
    }
    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerID(@PathVariable Long id) {
        Vuelo vuelo = vueloService.obtenerPorId(id);
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrados existosamente")
                        .vuelo(vueloMapper.vueloToVueloDTO(vuelo))
                        .build(),
                HttpStatus.OK
        );
    }
    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerDestino(@PathVariable Long id) {
        List<Vuelo> vuelos = vueloService.obtenerDestino(id);
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrados existosamente")
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),
                HttpStatus.OK
        );
    }
    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerOrigen(@PathVariable Long id) {
        List<Vuelo> vuelos =  vueloService.obtenerOrigen(id);
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrados existosamente")
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),
                HttpStatus.OK
        );
    }
    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerVueloFechaSalida(@RequestParam LocalDateTime fechaSalida){
        List<Vuelo> vuelos = vueloService.buscarPorFechaDeSalida(fechaSalida);
        return  new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrado existosamente")
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<MensajeRespondeVuelo> obtenerVueloFechaSalida(
            @RequestParam("fechaSalidaMin")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaSalidaMin,
            @RequestParam("fechaSalidaMax")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaSalidaMax)
    {
        List<Vuelo> vuelos = vueloService.buscarPorFechaDeSalida(fechaSalidaMin, fechaSalidaMax);
        return  new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Vuelos encontrado existosamente")
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),
                HttpStatus.OK
        );
    }

    @Override
    public String obtenerPropierties() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        PropiertiesVuelo propVuelo = new PropiertiesVuelo(configVuelos.getMsg(),configVuelos.getBuildVersion(),configVuelos.getMailDetails());
        return ow.writeValueAsString(propVuelo);
    }

    @Override
    public ResponseEntity<MensajeRespondeVuelo> confirmarReservaVuelo(@PathVariable Long id) {
        Vuelo vueloActualizado = vueloService.confirmarReserva(id);
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Cupo de vuelo confirmado exitosamente.")
                        .vuelo(vueloMapper.vueloToVueloDTO(vueloActualizado))
                        .build(),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<MensajeRespondeVuelo> cancelarReservaVuelo(@PathVariable Long id) {
        Vuelo vueloActualizado = vueloService.cancelarReserva(id);
        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje("Cupo de vuelo liberado exitosamente.")
                        .vuelo(vueloMapper.vueloToVueloDTO(vueloActualizado))
                        .build(),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<MensajeRespondeVuelo> buscarVuelos(
            @RequestParam String origen,
            @RequestParam String destino,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<Vuelo> vuelos = vueloService.buscarVuelosPor(origen, destino, fecha);

        String mensaje = vuelos.isEmpty()
                ? "No se encontraron vuelos para esa ruta y fecha."
                : "Vuelos encontrados exitosamente.";

        return new ResponseEntity<>(
                MensajeRespondeVuelo.builder()
                        .mensaje(mensaje)
                        .vueloLista(vueloMapper.vueloToVueloDTO(vuelos))
                        .build(),
                HttpStatus.OK
        );
    }
}
