package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ElementoExistenteExcepction extends RuntimeException {
    public ElementoExistenteExcepction(String message) {
        super(message);
    }
}
