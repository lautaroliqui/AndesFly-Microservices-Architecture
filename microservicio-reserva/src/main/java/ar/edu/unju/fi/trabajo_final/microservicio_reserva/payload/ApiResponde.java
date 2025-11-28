package ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class ApiResponde {
    private Date fecha = new Date();
    private String message;
    private String uri;

    public ApiResponde(String message, String uri) {
        this.message = message;
        this.uri = uri.replace("uri=","");
    }
}
