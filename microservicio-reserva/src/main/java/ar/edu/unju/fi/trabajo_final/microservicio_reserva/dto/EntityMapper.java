package ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.clienteDTO.ClienteDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.reservaDTO.ReservaDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.vueloDTO.VueloDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.ClienteRemote;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.VueloRemote;
import org.mapstruct.Mapper;


import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {


    ReservaDTO reservaToReservaDTO (Reserva reserva);

    List<ReservaDTO> listaReservaToListaReservaDTO (List<Reserva> reservas);

    VueloDTO vueloRemoteToVueloDTO (VueloRemote vueloRemote);

    ClienteDTO clienteRemoteToClienteDTO (ClienteRemote clienteRemote);

}
