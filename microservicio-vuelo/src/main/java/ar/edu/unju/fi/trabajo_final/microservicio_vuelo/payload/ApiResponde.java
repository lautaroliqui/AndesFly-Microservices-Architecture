package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ApiResponde {
    private Date fecha = new Date();
    private String message;
    private String uri;
    public ApiResponde(String mensaje, String uri) {
        this.message = mensaje;
        this.uri = uri.replaceAll("uri=", "");
    }
}
