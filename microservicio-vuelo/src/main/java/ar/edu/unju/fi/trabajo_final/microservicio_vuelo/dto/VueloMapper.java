package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.dto;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VueloMapper {

    @Mapping(target = "origen", expression = "java(combinarDestino(vuelo.getOrigen()))")
    @Mapping(target = "destino", expression = "java(combinarDestino(vuelo.getDestino()))")
    VueloDTO vueloToVueloDTO(Vuelo vuelo);

    List<VueloDTO> vueloToVueloDTO(List<Vuelo> vuelos);

    // Métodos auxiliares (no los ignores, MapStruct los puede usar)
    default String combinarDestino(Destino destino) {
        if (destino == null) return null;
        return "("+destino.getCodigo()+")- "+destino.getPais() + ": " + destino.getNombre();
    }

    DestinoDTO destinoToDestinoDTO(Destino destino);
    List<DestinoDTO> destinoToDestinoDTO(List<Destino> destinos);
}

