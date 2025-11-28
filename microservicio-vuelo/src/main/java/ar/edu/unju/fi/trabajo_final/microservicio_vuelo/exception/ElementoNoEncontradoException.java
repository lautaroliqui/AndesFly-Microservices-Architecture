package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ElementoNoEncontradoException extends RuntimeException {
    public ElementoNoEncontradoException(String message) {
        super(message);
    }
}
