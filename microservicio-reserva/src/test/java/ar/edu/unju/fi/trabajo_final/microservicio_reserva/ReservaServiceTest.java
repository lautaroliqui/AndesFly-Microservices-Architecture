package ar.edu.unju.fi.trabajo_final.microservicio_reserva;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.EntityMapper;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.dto.vueloDTO.VueloDTO;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.enums.EstadoReserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ElementoExistenteException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeCliente;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeVuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.ClienteRemote;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.remote.VueloRemote;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.repository.ReservaRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client.ClienteFeignClient;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.client.VuelosFeignClient;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.service.impl.ReservaServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReservaServiceMicroserviceTest {
    @Mock
    ReservaRepository reservaRepository;

    @Mock
    ClienteFeignClient clienteFeignClient;

    @Mock
    VuelosFeignClient vuelosFeignClient;

    @Mock
    EntityMapper entityMapper;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    ReservaServiceImpl reservaService;

    private Reserva reservaNueva;
    private Reserva reservaGeneradaExistente;
    private Reserva reservaConfirmadaExistente;
    private Reserva reservaCanceladaExistente;
    private VueloRemote vueloConCupo;
    private VueloDTO vueloDTOConCupo;
    private ClienteRemote clienteRemote;
    @BeforeEach
    void setUp() {

        vueloConCupo = VueloRemote.builder()
                .id(1L)
                .codigo("AA-101")
                .destino("Lima")
                .origen("España")
                .fechaLlegada(LocalDateTime.now().plusDays(2))
                .fechaSalida(LocalDateTime.now().plusDays(1))
                .cupoTotal(10).cupoReservado(5)
                .build();
        vueloDTOConCupo = VueloDTO.builder()
                .id(1L)
                .codigo("AA-101")
                .destino("Lima")
                .origen("España")
                .fechaLlegada(LocalDateTime.now().plusDays(2))
                .fechaSalida(LocalDateTime.now().plusDays(1))
                .cupoTotal(10).cupoReservado(5)
                .build();
        clienteRemote = ClienteRemote.builder().id(10L).email("").documento("2123321").nombreCompleto("Luis Tolay").build();
        reservaNueva = Reserva.builder()
                .codigo("RES-NEW")
                .clienteId(10L)
                .vueloId(1L)
                .observaciones("Nueva reserva test")
                .build();

        reservaGeneradaExistente = Reserva.builder()
                .id(100L).codigo("RES-GEN").estado(EstadoReserva.GENERADA)
                .clienteId(10L).vueloId(1L).fechaCreacion(LocalDate.now().minusDays(1))
                .build();

        reservaConfirmadaExistente = Reserva.builder()
                .id(101L).codigo("RES-CON").estado(EstadoReserva.CONFIRMADA)
                .clienteId(10L).vueloId(1L)
                .build();

        reservaCanceladaExistente = Reserva.builder()
                .id(102L).codigo("RES-CAN").estado(EstadoReserva.CANCELADA)
                .clienteId(10L).vueloId(1L)
                .build();
    }

    // =====================================================
    // ====================== CREAR =========================
    // =====================================================

    @Test
    @DisplayName("CREAR-01: Debe crear reserva si código es único y Cliente/Vuelo existen (via Feign)")
    void testCrear_Exito() {

        when(reservaRepository.findByCodigo("RES-NEW")).thenReturn(Optional.empty());

        when(vuelosFeignClient.obtenerVueloPorId(1L))
                .thenReturn(new MensajeRespondeVuelo("",vueloConCupo,null));

        when(clienteFeignClient.obtenerClientePorId(10L))
                .thenReturn(new MensajeRespondeCliente("",      clienteRemote,
                        null));

        Reserva saved = Reserva.builder()
                .id(200L)
                .codigo("RES-NEW")
                .clienteId(10L)
                .vueloId(1L)
                .estado(EstadoReserva.GENERADA)
                .build();

        when(reservaRepository.save(any())).thenReturn(saved);

        Reserva resultado = reservaService.crear(reservaNueva);

        assertNotNull(resultado);
        assertEquals(EstadoReserva.GENERADA, resultado.getEstado());
        verify(reservaRepository).save(any());
    }

    @Test
    @DisplayName("CREAR-02: Debe fallar con ElementoExistenteException si el código ya existe")
    void testCrear_CodigoDuplicado() {

        when(reservaRepository.findByCodigo("RES-NEW"))
                .thenReturn(Optional.of(reservaGeneradaExistente));

        assertThrows(ElementoExistenteException.class, () -> reservaService.crear(reservaNueva));
    }

    @Test
    @DisplayName("CREAR-03: Debe fallar si Cliente responde 404")
    void testCrear_ClienteNoEncontrado() {

        when(reservaRepository.findByCodigo("RES-NEW")).thenReturn(Optional.empty());
        when(vuelosFeignClient.obtenerVueloPorId(1L))
                .thenReturn(new MensajeRespondeVuelo("",vueloConCupo,null));

        when(clienteFeignClient.obtenerClientePorId(10L))
                .thenThrow(feign.FeignException.NotFound.class);

        assertThrows(feign.FeignException.NotFound.class, () -> reservaService.crear(reservaNueva));
    }

    @Test
    @DisplayName("CREAR-04: Debe fallar si Vuelo responde 404")
    void testCrear_VueloNoEncontrado() {

        when(reservaRepository.findByCodigo("RES-NEW")).thenReturn(Optional.empty());
        when(vuelosFeignClient.obtenerVueloPorId(1L))
                .thenThrow(feign.FeignException.NotFound.class);

        assertThrows(feign.FeignException.NotFound.class, () -> reservaService.crear(reservaNueva));
    }

    // =====================================================
    // ==================== CONFIRMAR ======================
    // =====================================================

    @Test
    @DisplayName("CONFIRMAR-01: Debe confirmar si ms-vuelos responde OK y hay cupo")
    void testConfirmar_Exito() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reservaGeneradaExistente));

        MensajeRespondeVuelo mensajeVuelo = new MensajeRespondeVuelo("",vueloConCupo,null);

        when(vuelosFeignClient.obtenerVueloPorId(1L))
                .thenReturn(mensajeVuelo);

        when(entityMapper.vueloRemoteToVueloDTO(vueloConCupo))
                .thenReturn(vueloDTOConCupo);

        when(reservaRepository.save(any())).thenReturn(reservaGeneradaExistente);

        Reserva resultado = reservaService.confirmar(100L);

        assertEquals(EstadoReserva.CONFIRMADA, resultado.getEstado());
        verify(vuelosFeignClient).confirmarVuelo(1L);
    }

    @Test
    @DisplayName("CONFIRMAR-02: Debe lanzar ValidacionException si no hay cupo")
    void testConfirmar_SinCupo() {

        VueloRemote vueloSinCupo = VueloRemote.builder()
                .id(1L).codigo("AA-101")
                .origen("").destino("").fechaLlegada(LocalDateTime.now())
                .fechaSalida(LocalDateTime.now()).cupoTotal(5).cupoReservado(5).build();
        VueloDTO vueloDTOSinCupo = VueloDTO.builder().id(1L).codigo("AA-101").build();
        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reservaGeneradaExistente));

        when(vuelosFeignClient.obtenerVueloPorId(1L))
                .thenReturn(new MensajeRespondeVuelo("",vueloSinCupo,null));

        when(entityMapper.vueloRemoteToVueloDTO(vueloSinCupo))
                .thenReturn(vueloDTOSinCupo);

        assertThrows(ValidacionException.class, () -> reservaService.confirmar(100L));
    }

    @Test
    @DisplayName("CONFIRMAR-03: Debe lanzar ValidacionException si el estado no es GENERADA")
    void testConfirmar_EstadoIncorrecto() {

        when(reservaRepository.findById(101L))
                .thenReturn(Optional.of(reservaConfirmadaExistente));

        assertThrows(ValidacionException.class, () -> reservaService.confirmar(101L));
    }

    // =====================================================
    // ===================== CANCELAR ======================
    // =====================================================

    @Test
    @DisplayName("CANCELAR-01: Debe cancelar y llamar a ms-vuelos si estaba CONFIRMADA")
    void testCancelar_Confirmada_Exito() {

        when(reservaRepository.findById(101L))
                .thenReturn(Optional.of(reservaConfirmadaExistente));

        MensajeRespondeVuelo msg = new MensajeRespondeVuelo("",vueloConCupo,null);
        when(vuelosFeignClient.obtenerVueloPorId(1L)).thenReturn(msg);
        when(entityMapper.vueloRemoteToVueloDTO(vueloConCupo)).thenReturn(vueloDTOConCupo);
        when(reservaRepository.save(any())).thenReturn(reservaConfirmadaExistente);

        Reserva res = reservaService.cancelar(101L);

        assertEquals(EstadoReserva.CANCELADA, res.getEstado());
        verify(vuelosFeignClient).cancelarVuelo(1L);
    }

    @Test
    @DisplayName("CANCELAR-02: Debe cancelar SIN llamar a ms-vuelos si estaba GENERADA")
    void testCancelar_Generada_Exito() {

        when(reservaRepository.findById(100L))
                .thenReturn(Optional.of(reservaGeneradaExistente));

        MensajeRespondeVuelo msg = new MensajeRespondeVuelo("",vueloConCupo,null);
        when(vuelosFeignClient.obtenerVueloPorId(1L)).thenReturn(msg);
        when(entityMapper.vueloRemoteToVueloDTO(vueloConCupo)).thenReturn(vueloDTOConCupo);
        when(reservaRepository.save(any())).thenReturn(reservaGeneradaExistente);

        Reserva res = reservaService.cancelar(100L);

        assertEquals(EstadoReserva.CANCELADA, res.getEstado());
        verify(vuelosFeignClient, never()).cancelarVuelo(anyLong());
    }

    @Test
    @DisplayName("CANCELAR-03: Debe lanzar ValidacionException si ya está CANCELADA")
    void testCancelar_YaCancelada() {

        when(reservaRepository.findById(102L))
                .thenReturn(Optional.of(reservaCanceladaExistente));

        assertThrows(ValidacionException.class, () -> reservaService.cancelar(102L));
        verify(reservaRepository, never()).save(any());
    }

    // =====================================================
    // ====================== BUSCAR =======================
    // =====================================================

    @Test
    @DisplayName("BUSCAR-01: Debe retornar lista completa de reservas")
    void testBuscarTodasReserva() {
        List<Reserva> lista = List.of(reservaConfirmadaExistente, reservaGeneradaExistente);

        when(reservaRepository.findAll((Sort) any())).thenReturn(lista);

        List<Reserva> result = reservaService.buscarTodasReserva();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("BUSCAR-02: Debe retornar una reserva por ID")
    void testBuscarReservaId_Exito() {

        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaGeneradaExistente));

        Reserva res = reservaService.buscarReservaId(100L);

        assertNotNull(res);
        assertEquals(100L, res.getId());
    }

    @Test
    @DisplayName("BUSCAR-03: Debe lanzar excepción si ID no existe")
    void testBuscarReservaId_NoEncontrado() {

        when(reservaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> reservaService.buscarReservaId(999L));
    }
}

