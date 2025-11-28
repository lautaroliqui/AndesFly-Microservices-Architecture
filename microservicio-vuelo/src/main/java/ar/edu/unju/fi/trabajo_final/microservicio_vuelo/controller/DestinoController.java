package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto.VueloMapper;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.MensajeRespondeDestino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.MensajeRespondeVuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl.DestinoServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1_1/")
@Tag(name = "Destino", description = "Controlador para Destino")
public class DestinoController implements ApiDestino{

    @Autowired
    private DestinoServiceImpl destinoService;
    @Autowired
    private VueloMapper destinoMapper;

    @Override
    public ResponseEntity<MensajeRespondeDestino> crearDestino(@RequestBody Destino destino) {
        Destino creadoDestino = destinoService.crear(destino);
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("Se registro existosamente el destino.")
                .destino(null)
                .build()
        , HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> actualizarDestino(@RequestBody Destino destino) {
        Destino actualizadoDestino = destinoService.actualizar(destino);
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("Se actualizo existosamente el destino.")
                .destino(null)
                .destinoLista(null)
                .build()
                , HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> eliminarDestino(@PathVariable("id") Long id) {
        destinoService.eliminar(id);
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("Se elimino el destino con el ID: "+ id)
                .destino(null)
                .destinoLista(null)
                .build()
                , HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> obtenerDestinos() {

        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("")
                .destino(null)
                .destinoLista(destinoMapper.destinoToDestinoDTO(destinoService.listarTodos()))
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> obtenerID(@PathVariable("id") Long id) {
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("")
                .destino(destinoMapper.destinoToDestinoDTO(destinoService.buscarPorID(id)))
                .destinoLista(null)
                .build(),
            HttpStatus.OK);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> obtenerCodigo(@RequestParam String codigo) {
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("")
                .destino(destinoMapper.destinoToDestinoDTO(destinoService.buscarPorCodigo(codigo)))
                .destinoLista(null)
                .build(),
                HttpStatus.OK);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> obtenerPais(@RequestParam String pais) {
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("")
                .destino(null)
                .destinoLista(destinoMapper.destinoToDestinoDTO(destinoService.buscarPorPais(pais)))
                .build(),
                HttpStatus.OK);
    }
    @Override
    public ResponseEntity<MensajeRespondeDestino> obtenerNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(MensajeRespondeDestino.builder()
                .mensaje("")
                .destino(null)
                .destinoLista(destinoMapper.destinoToDestinoDTO(destinoService.buscarPorNombre(nombre)))
                .build(),
                HttpStatus.OK);
    }
}
