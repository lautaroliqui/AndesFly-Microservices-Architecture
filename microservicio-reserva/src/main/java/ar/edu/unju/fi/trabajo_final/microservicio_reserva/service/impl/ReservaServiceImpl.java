package ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.clienteDTO.ClienteDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.EntityMapper;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.reservaDTO.ReservaDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.vueloDTO.VueloDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.enums.EstadoReserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ElementoExistenteException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeCliente;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeVuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.repository.ReservaRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.ReservaService;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client.ClienteFeignClient;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client.VuelosFeignClient;
import feign.FeignException;
import org.springframework.data.domain.Sort; // <-- Importación corregida
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final EntityMapper entityMapper;
    private final ClienteFeignClient clienteFeignClient;
    private final VuelosFeignClient  vuelosFeignClient;

    public ReservaServiceImpl(ReservaRepository reservaRepository, EntityMapper entityMapper, ClienteFeignClient clienteFeignClient, VuelosFeignClient vuelosFeignClient) {
        this.reservaRepository = reservaRepository;
        this.entityMapper = entityMapper;
        this.clienteFeignClient = clienteFeignClient;
        this.vuelosFeignClient = vuelosFeignClient;
    }

    // --------------------------- CREAR RESERVA ---------------------------
    @Override
    @Transactional
    public Reserva crear(Reserva reserva) {
        reservaRepository.findByCodigo(reserva.getCodigo())
                .ifPresent(r -> {
                    throw new ElementoExistenteException("El código de reserva ya existe");
                });
        vuelosFeignClient.obtenerVueloPorId(reserva.getVueloId());
        clienteFeignClient.obtenerClientePorId(reserva.getClienteId());
        reserva.setEstado(EstadoReserva.GENERADA);
        reserva.setFechaCreacion(LocalDate.now());
        return reservaRepository.save(reserva);
    }



    @Override
    @Transactional
    public Reserva confirmar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ElementoNoEncontradoException("No existe una reserva con el id: " + id));
        if (!EstadoReserva.GENERADA.equals(reserva.getEstado())) {
            throw new ValidacionException("No se puede CONFIRMAR una reserva cuyo estado no sea GENERADO");
        }
        MensajeRespondeVuelo mensaje = vuelosFeignClient.obtenerVueloPorId(reserva.getVueloId());
        VueloDTO vuelo = entityMapper.vueloRemoteToVueloDTO(mensaje.getVuelo());
        if (vuelo.getCupoReservado() + 1 > vuelo.getCupoTotal()) {
            throw new ValidacionException("El vuelo ha alcanzado el cupo total de " + vuelo.getCupoTotal());
        }
        vuelosFeignClient.confirmarVuelo(vuelo.getId());
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Override
    @Transactional
    public Reserva cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ElementoNoEncontradoException("No existe una reserva con el id: " + id));
        if (EstadoReserva.CANCELADA.equals(reserva.getEstado())) {
            throw new ValidacionException("La reserva con id " + id + " ya está CANCELADA");
        }
        MensajeRespondeVuelo mensaje = vuelosFeignClient.obtenerVueloPorId(reserva.getVueloId());
        VueloDTO vuelo = entityMapper.vueloRemoteToVueloDTO(mensaje.getVuelo());
        if (EstadoReserva.CONFIRMADA.equals(reserva.getEstado())) {
            vuelo.setCupoReservado(vuelo.getCupoReservado() - 1);
            vuelosFeignClient.cancelarVuelo(vuelo.getId());
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }


    @Override
    public List<Reserva> buscarTodasReserva(){
        return reservaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }
    @Override
    public Reserva buscarReservaId(Long id){
        return reservaRepository.findById(id).orElseThrow(() -> new ElementoNoEncontradoException("No existe una reserva con el id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> buscarReservasPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    @Transactional
    public Reserva crearReservaParaUsuario(Reserva reserva, Long clienteId) {
        reservaRepository.findByCodigo(reserva.getCodigo())
                .ifPresent(r -> {
                    throw new ElementoExistenteException("El código de reserva ya existe");
                });

        vuelosFeignClient.obtenerVueloPorId(reserva.getVueloId());
        clienteFeignClient.obtenerClientePorId(clienteId);

        reserva.setClienteId(clienteId);
        reserva.setEstado(EstadoReserva.GENERADA);
        reserva.setFechaCreacion(LocalDate.now());
        return reservaRepository.save(reserva);
    }

    // ----- INICIO DE MÉTODOS MODIFICADOS -----

    @Override
    public ReservaDTO clienteVueloEsamblador(Reserva reserva){
        ReservaDTO dto = entityMapper.reservaToReservaDTO(reserva);

        VueloDTO vueloDTO = null;
        ClienteDTO clienteDTO = null;

        try {
            MensajeRespondeVuelo mensajeVuelo = vuelosFeignClient.obtenerVueloPorId(reserva.getVueloId());
            if (mensajeVuelo != null && mensajeVuelo.getVuelo() != null) {
                vueloDTO = entityMapper.vueloRemoteToVueloDTO(mensajeVuelo.getVuelo());
            }
        } catch (FeignException.NotFound e) {
            System.err.println("Advertencia: No se encontró el Vuelo con ID: " + reserva.getVueloId());
        }

        try {
            MensajeRespondeCliente mensajeCliente = clienteFeignClient.obtenerClientePorId(reserva.getClienteId());
            if (mensajeCliente != null && mensajeCliente.getCliente() != null) {
                // ----- ¡AQUÍ ESTÁ LA CORRECCIÓN DEL TIPEO! -----
                clienteDTO =  entityMapper.clienteRemoteToClienteDTO(mensajeCliente.getCliente());
            }
        } catch (FeignException.NotFound e) {
            System.err.println("Advertencia: No se encontró el Cliente con ID: " + reserva.getClienteId());
        }

        if (vueloDTO != null) {
            if (dto.getCodigoVuelo()==null){
                dto.setCodigoVuelo(vueloDTO.getCodigo());
            }
            if (dto.getOrigen()==null){
                dto.setOrigen(vueloDTO.getOrigen());
            }
            if (dto.getDestino()==null){
                dto.setDestino(vueloDTO.getDestino());
            }
        } else {
            dto.setCodigoVuelo("[Vuelo Eliminado]");
            dto.setOrigen("[Vuelo Eliminado]");
            dto.setDestino("[Vuelo Eliminado]");
        }
        if (clienteDTO != null) {
            if (dto.getCliente()==null){
                dto.setCliente(clienteDTO.getNombreCompleto());
            }
        } else {
            dto.setCliente("[Cliente Eliminado]");
        }

        return dto;
    }

    @Override
    public List<ReservaDTO> clienteVueloEsamblador(List<Reserva> reservas){
        List<ReservaDTO> dtos = new ArrayList<>();
        for (Reserva reserva : reservas) {
            dtos.add(clienteVueloEsamblador(reserva));
        }
        return dtos;
    }
}