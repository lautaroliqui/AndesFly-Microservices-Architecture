package ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client;


import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("cliente")
public interface ClienteFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "api/v1_1/clientes/{id}", consumes = "application/json")
    MensajeRespondeCliente obtenerClientePorId (@PathVariable Long id);
}
