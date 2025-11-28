package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoExistenteExcepction;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.DestinoRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.VueloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VueloServiceImplTest {
    @Mock
    private VueloRepository vueloRepository;
    @Mock
    private DestinoRepository destinoRepository;

    @InjectMocks
    private VueloServiceImpl vueloService;

    private Vuelo vuelo, vueloActualizar, vuelo2;
    private Destino destinoOrigen1, destinoDestino1,destinoOrigen2, destinoDestino2;

    @BeforeEach
    void setUp() {
        destinoOrigen1 = Destino.builder()
                .id(1L)
                .pais("argentina")
                .nombre("jujuy")
                .codigo("ARGJY001")
                .build();
        destinoDestino1 = Destino.builder()
                .pais("argentina")
                .nombre("buenos aires")
                .codigo("ARGBB009")
                .build();
        destinoOrigen2 = Destino.builder()
                .id(1L)
                .pais("españa")
                .nombre("madrid")
                .codigo("ESPMD532")
                .build();
        destinoDestino2 = Destino.builder()
                .pais("peru")
                .nombre("lima")
                .codigo("PL769")
                .build();

        vuelo = Vuelo.builder()
                .codigo("AR123")
                .origen(destinoOrigen1)
                .destino(destinoDestino1)
                .fechaSalida(LocalDateTime.now().plusDays(1))
                .fechaLlegada(LocalDateTime.now().plusDays(2))
                .cupoTotal(10)
                .cupoReservado(3)
                .build();
        vueloActualizar = Vuelo.builder()
                .id(1L)
                .codigo("JA32")
                .origen(destinoOrigen2)
                .destino(destinoOrigen1)
                .fechaSalida(LocalDateTime.now().plusDays(1))
                .fechaLlegada(LocalDateTime.now().plusDays(2))
                .cupoTotal(10)
                .cupoReservado(3)
                .build();
        vuelo2 = Vuelo.builder()
                .id(2L)
                .codigo("AR123")
                .origen(destinoDestino1)
                .destino(destinoDestino2)
                .fechaSalida(LocalDateTime.now().plusDays(1))
                .fechaLlegada(LocalDateTime.now().plusDays(2))
                .cupoTotal(210)
                .cupoReservado(3)
                .build();
    }

    @Test
    void crearTest_Existoso() {
        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.findById(vuelo.getDestino().getId())).thenReturn(Optional.of(destinoDestino1));
        when(destinoRepository.findById(vuelo.getOrigen().getId())).thenReturn(Optional.of(destinoOrigen1));
        when(vueloRepository.save(vuelo)).thenReturn(vuelo);
       //

        Vuelo vueloEsperado = vueloService.crear(vuelo);

        assertNotNull(vueloEsperado);
        assertEquals(vuelo.getCodigo(), vueloEsperado.getCodigo());
        verify(vueloRepository, times(1)).findByCodigo(vuelo.getCodigo());
        verify(destinoRepository, times(1)).findById(vuelo.getDestino().getId());
        verify(destinoRepository, times(1)).findById(vuelo.getOrigen().getId());
        verify(vueloRepository, times(1)).save(vuelo);

    }
    @Test
    void crearTest_Codigo_Existente() {
        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.of(vuelo2));

        assertThrows(ElementoExistenteExcepction.class, () -> vueloService.crear(vuelo));
    }
    @Test
    void crearTest_Cupos_NoValido() {
        vuelo.setCupoReservado(11);
        vuelo.setCupoTotal(8);
        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.findById(vuelo.getDestino().getId())).thenReturn(Optional.of(destinoDestino1));
        when(destinoRepository.findById(vuelo.getOrigen().getId())).thenReturn(Optional.of(destinoOrigen1));

        assertThrows(ValidacionException.class, () -> vueloService.crear(vuelo));
        verify(vueloRepository, times(1)).findByCodigo(vuelo.getCodigo());
        verify(destinoRepository, times(1)).findById(vuelo.getDestino().getId());
        verify(destinoRepository, times(1)).findById(vuelo.getOrigen().getId());
    }

    @Test
    void crearTest_Fechas_NoValido() {
        vuelo.setFechaSalida(LocalDateTime.now().plusDays(5));
        vuelo.setFechaLlegada(LocalDateTime.now().plusDays(2));
        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.findById(vuelo.getDestino().getId())).thenReturn(Optional.of(destinoDestino1));
        when(destinoRepository.findById(vuelo.getOrigen().getId())).thenReturn(Optional.of(destinoOrigen1));

        assertThrows(ValidacionException.class, () -> vueloService.crear(vuelo));
        verify(vueloRepository, times(1)).findByCodigo(vuelo.getCodigo());
        verify(destinoRepository, times(1)).findById(vuelo.getDestino().getId());
        verify(destinoRepository, times(1)).findById(vuelo.getOrigen().getId());
    }
    @Test
    void crearTest_Origen_NoValido() {

        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.findById(vuelo.getDestino().getId())).thenReturn(Optional.of(destinoDestino1));
        when(destinoRepository.findById(vuelo.getOrigen().getId())).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.crear(vuelo));
        verify(vueloRepository, times(1)).findByCodigo(vuelo.getCodigo());
        verify(destinoRepository, times(1)).findById(vuelo.getDestino().getId());
        verify(destinoRepository, times(1)).findById(vuelo.getOrigen().getId());
    }
    @Test
    void crearTest_Destino_NoValido() {

        when(vueloRepository.findByCodigo(vuelo.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.findById(vuelo.getDestino().getId())).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.crear(vuelo));
        verify(vueloRepository, times(1)).findByCodigo(vuelo.getCodigo());
        verify(destinoRepository, times(1)).findById(vuelo.getDestino().getId());
    }

    //  <--------TEST ELIMINAR---------->
    @Test
    void eliminar_Id_Existoso(){
        when(vueloRepository.findById(1L)).thenReturn(Optional.of(vuelo));

        vueloService.eliminar(1L);

        verify(vueloRepository, times(1)).findById(1L);
    }

    @Test
    void eliminar_Id_ElementoExistenteExceprion() {
        when(vueloRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.eliminar(1L));

    }

    // <-------- TEST ACTUALIZAR ---------->
    @Test
    void actualizarTest_Exitoso() {
        vueloActualizar.setDestino(destinoDestino1);
        vueloActualizar.setOrigen(destinoOrigen1);

        when(vueloRepository.findById(vueloActualizar.getId())).thenReturn(Optional.of(vueloActualizar));
        when(vueloRepository.findByCodigo(vueloActualizar.getCodigo())).thenReturn(Optional.of(vueloActualizar));
        when(destinoRepository.findById(destinoDestino1.getId())).thenReturn(Optional.of(destinoDestino1));
        when(destinoRepository.findById(destinoOrigen1.getId())).thenReturn(Optional.of(destinoOrigen1));
        when(vueloRepository.save(vueloActualizar)).thenReturn(vueloActualizar);

        Vuelo actualizado = vueloService.actualizar(vueloActualizar);

        assertNotNull(actualizado);
        assertEquals(vueloActualizar.getCodigo(), actualizado.getCodigo());
        verify(vueloRepository, times(1)).findById(vueloActualizar.getId());
        verify(vueloRepository, times(1)).save(vueloActualizar);
    }

    @Test
    void actualizarTest_VueloNoExistente() {
        when(vueloRepository.findById(vueloActualizar.getId())).thenReturn(Optional.empty());
        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.actualizar(vueloActualizar));
    }

    @Test
    void actualizarTest_CodigoExistenteDistintoId() {
        Vuelo otroVuelo = Vuelo.builder().id(99L).codigo("JA32").build();
        when(vueloRepository.findById(vueloActualizar.getId())).thenReturn(Optional.of(vueloActualizar));
        when(vueloRepository.findByCodigo(vueloActualizar.getCodigo())).thenReturn(Optional.of(otroVuelo));

        assertThrows(ElementoExistenteExcepction.class, () -> vueloService.actualizar(vueloActualizar));
    }

    @Test
    void actualizarTest_DestinoNoEncontrado() {
        vueloActualizar.setDestino(destinoDestino1);
        vueloActualizar.setOrigen(destinoOrigen1);

        when(vueloRepository.findById(vueloActualizar.getId())).thenReturn(Optional.of(vueloActualizar));
        when(vueloRepository.findByCodigo(vueloActualizar.getCodigo())).thenReturn(Optional.of(vueloActualizar));
        when(destinoRepository.findById(vueloActualizar.getDestino().getId())).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.actualizar(vueloActualizar));
    }


    // <-------- TEST CONSULTAS ---------->
    @Test
    void obtenerPorId_Exitoso() {
        when(vueloRepository.findById(1L)).thenReturn(Optional.of(vuelo));
        Vuelo result = vueloService.obtenerPorId(1L);
        assertEquals(vuelo.getCodigo(), result.getCodigo());
        verify(vueloRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_NoExistente() {
        when(vueloRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.obtenerPorId(1L));
    }

    @Test
    void obtenerOrigen_Exitoso() {
        when(vueloRepository.findByOrigen_Id(1L)).thenReturn(java.util.List.of(vuelo));
        var lista = vueloService.obtenerOrigen(1L);
        assertFalse(lista.isEmpty());
        verify(vueloRepository, times(1)).findByOrigen_Id(1L);
    }

    @Test
    void obtenerOrigen_NoExistente() {
        when(vueloRepository.findByOrigen_Id(1L)).thenReturn(java.util.List.of());
        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.obtenerOrigen(1L));
    }

    @Test
    void obtenerDestino_Exitoso() {
        when(vueloRepository.findByDestino_Id(1L)).thenReturn(java.util.List.of(vuelo));
        var lista = vueloService.obtenerDestino(1L);
        assertFalse(lista.isEmpty());
        verify(vueloRepository, times(1)).findByDestino_Id(1L);
    }

    @Test
    void obtenerDestino_NoExistente() {
        when(vueloRepository.findByDestino_Id(1L)).thenReturn(java.util.List.of());
        assertThrows(ElementoNoEncontradoException.class, () -> vueloService.obtenerDestino(1L));
    }

    @Test
    void buscarPorFechaDeSalida_Exacta() {
        var fecha = LocalDateTime.now().plusDays(1);
        when(vueloRepository.findByFechaSalida(fecha)).thenReturn(java.util.List.of(vuelo));
        var lista = vueloService.buscarPorFechaDeSalida(fecha);
        assertFalse(lista.isEmpty());
        verify(vueloRepository, times(1)).findByFechaSalida(fecha);
    }

    @Test
    void buscarPorFechaDeSalida_Rango_Exitoso() {
        LocalDateTime min = LocalDateTime.now();
        LocalDateTime max = LocalDateTime.now().plusDays(5);
        when(vueloRepository.findByFechaSalidaBetween(min, max)).thenReturn(java.util.List.of(vuelo));
        var lista = vueloService.buscarPorFechaDeSalida(min, max);
        assertFalse(lista.isEmpty());
        verify(vueloRepository, times(1)).findByFechaSalidaBetween(min, max);
    }

    @Test
    void buscarPorFechaDeSalida_Rango_Invalido() {
        LocalDateTime min = LocalDateTime.now().plusDays(5);
        LocalDateTime max = LocalDateTime.now();
        assertThrows(ValidacionException.class, () -> vueloService.buscarPorFechaDeSalida(min, max));
    }


}