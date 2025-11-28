package ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.ApiResponde;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ElementoNoEncontradoException.class)
    public ResponseEntity<ApiResponde> handlerElementoNoEncontradoException(ElementoNoEncontradoException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ElementoExistenteException.class)
    public ResponseEntity<ApiResponde> handlerElementoExistenteExcepction(ElementoExistenteException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(EntidadNoEncontradoException.class)
    public ResponseEntity<ApiResponde> handlerEntidadNoEncontradoException(EntidadNoEncontradoException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ApiResponde> handlerValidacionException(ValidacionException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiResponde> handlerFeignExceptionNotFound(FeignException.NotFound e){
        String json = e.contentUTF8();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ApiResponde apiResponde = objectMapper.readValue(json, ApiResponde.class);
            return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ApiResponde> handlerFeignExceptionBadRequest(FeignException.BadRequest e){
        String json = e.contentUTF8();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponde apiResponde = objectMapper.readValue(json, ApiResponde.class);
            return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

}
