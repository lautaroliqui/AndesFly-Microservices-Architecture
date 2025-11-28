package ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception;

public class ElementoExistenteException extends RuntimeException {
    public ElementoExistenteException(String message) {
        super(message);
    }
}
