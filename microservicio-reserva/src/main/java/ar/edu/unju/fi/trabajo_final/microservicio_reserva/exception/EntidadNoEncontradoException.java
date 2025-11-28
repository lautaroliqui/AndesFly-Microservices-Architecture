package ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception;

import org.springframework.http.HttpStatusCode;

public class EntidadNoEncontradoException extends RuntimeException {
    private final HttpStatusCode status;
    public EntidadNoEncontradoException(String message, HttpStatusCode status) {
        super(message);
        this.status = status;
    }
}
