package ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception;

public class ElementoNoEncontradoException extends RuntimeException {
    public ElementoNoEncontradoException(String message) {
        super(message);
    }
}
