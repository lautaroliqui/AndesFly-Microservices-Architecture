package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.ApiResponde;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ElementoExistenteExcepction.class)
    public ResponseEntity<ApiResponde> handlerElementoExistenteExcepction(ElementoExistenteExcepction e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<ApiResponde> handlerValidacionException(ValidacionException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ElementoNoEncontradoException.class)
    public ResponseEntity<ApiResponde> handlerElementoNoEncontradoException(ElementoNoEncontradoException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
    }
}
