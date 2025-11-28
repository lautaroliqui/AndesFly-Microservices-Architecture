package ar.edu.unju.fi.trabajo_final.microservicio_cliente.dto;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteDTO clienteToClienteDTO(Cliente cliente);

    List<ClienteDTO> listaClienteTolistaClienteDTO(List<Cliente> clientes);
}
