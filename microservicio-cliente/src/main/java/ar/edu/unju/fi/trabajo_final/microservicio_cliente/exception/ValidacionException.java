package ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception;

public class ValidacionException extends RuntimeException {
    public ValidacionException(String message) {
        super(message);
    }
}