package ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception;

public class ElementoNoEncontradoException extends RuntimeException {
    public ElementoNoEncontradoException(String message) {
        super(message);
    }
}