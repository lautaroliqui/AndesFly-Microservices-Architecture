package ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor

public class ApiResponde {
    private Date fecha = new Date();
    private String message;
    private String uri;

    public ApiResponde(String message, String url) {
        this.message = message;
        this.uri = url.replace("uri=","");
    }
}
