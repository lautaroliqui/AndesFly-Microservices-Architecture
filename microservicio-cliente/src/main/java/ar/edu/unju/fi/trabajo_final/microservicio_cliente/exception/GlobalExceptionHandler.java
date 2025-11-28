package ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload.ApiResponde;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

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
        return new ResponseEntity<>(apiResponde, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ElementoNoEncontradoException.class)
    public ResponseEntity<ApiResponde> handlerElementoNoEncontradoException(ElementoNoEncontradoException e, WebRequest webRequest){
        ApiResponde apiResponde = new ApiResponde(e.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(apiResponde, HttpStatus.NOT_FOUND);
    }
    /**
     * Maneja las excepciones de validación de @Valid (formato de email, campos no vacíos, etc.).
     * Devuelve un 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
