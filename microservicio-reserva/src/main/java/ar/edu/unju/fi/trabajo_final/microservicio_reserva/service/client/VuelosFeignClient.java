package ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeVuelo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("vuelo")
public interface VuelosFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "api/v1_1/vuelos/{id}", consumes = "application/json")
    MensajeRespondeVuelo obtenerVueloPorId (@PathVariable Long id);

    @RequestMapping(method = RequestMethod.PUT, value = "api/v1_1/vuelo/confirmar/{id}", consumes = "application/json")
    MensajeRespondeVuelo confirmarVuelo(@PathVariable Long id);

    @RequestMapping(method = RequestMethod.PUT, value = "api/v1_1/vuelo/cancelar/{id}",consumes = "application/json")
    MensajeRespondeVuelo cancelarVuelo(@PathVariable Long id);

}
