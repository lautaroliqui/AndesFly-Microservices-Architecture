package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoExistenteExcepction;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.DestinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DestinoServiceImplTest {

    @Mock
    private DestinoRepository destinoRepository;
    @InjectMocks
    private DestinoServiceImpl destinoServiceImpl;

    private Destino destino, destinoTest;

    @BeforeEach
    void setUp() {
        destino = Destino.builder()
                .id(1L)
                .pais("Argentina")
                .nombre("Jujuy")
                .codigo("ARGJY001")
                .build();
        destinoTest = Destino.builder()
                .pais("Argentina")
                .nombre("Jujuy")
                .codigo("ARGJY001")
                .build();

    }
    // <----------TEST CREAR------->
    @Test
    void crearExistoso(){
        when(destinoRepository.findByCodigo(destinoTest.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.save(destinoTest)).thenReturn(destino);

        Destino destinoCreado =  destinoServiceImpl.crear(destinoTest);

        assertNotNull(destinoCreado);
        assertNotNull(destinoCreado.getId());


        verify(destinoRepository,times(1)).findByCodigo(destinoTest.getCodigo());
        verify(destinoRepository,times(1)).save(destinoTest);
    }
    @Test
    void crearCodigoExistente(){
        when(destinoRepository.findByCodigo(destinoTest.getCodigo())).thenReturn(Optional.of(destino));
        assertThrows(ElementoExistenteExcepction.class,()->destinoServiceImpl.crear(destinoTest));

        verify(destinoRepository,times(1)).findByCodigo(destinoTest.getCodigo());
    }
    // <----------TEST ACTUALIZAR------->
    @Test
    void actualizarExistoso() {
        destinoTest.setId(1L);

        when(destinoRepository.findById(destinoTest.getId())).thenReturn(Optional.of(destino));
        when(destinoRepository.findByCodigo(destinoTest.getCodigo())).thenReturn(Optional.empty());
        when(destinoRepository.save(any(Destino.class))).thenReturn(destinoTest);

        Destino destinoActualizado = destinoServiceImpl.actualizar(destinoTest);

        assertNotNull(destinoActualizado);
        assertEquals(destinoActualizado.getCodigo(), destinoTest.getCodigo());
        assertEquals(destinoActualizado.getPais().toLowerCase(), destinoTest.getPais().toLowerCase());
        assertEquals(destinoActualizado.getNombre().toLowerCase(), destinoTest.getNombre().toLowerCase());

        verify(destinoRepository, times(1)).findById(destinoTest.getId());
        verify(destinoRepository, times(1)).findByCodigo(destinoTest.getCodigo());
        verify(destinoRepository, times(1)).save(any(Destino.class));
    }
    @Test
    void actualizar_CodigoExistente_AOtroUsuario(){
        Destino destinoActualizar = Destino.builder().id(3L).codigo("ARGJY001").nombre("Salta").pais("Argentina").build();
        when(destinoRepository.findById(destinoActualizar.getId())).thenReturn(Optional.of(destinoActualizar));
        when(destinoRepository.findByCodigo(destinoActualizar.getCodigo())).thenReturn(Optional.of(destino));

        assertThrows(ElementoExistenteExcepction.class,()->destinoServiceImpl.actualizar(destinoActualizar));
        verify(destinoRepository,times(1)).findById(destinoActualizar.getId());
        verify(destinoRepository,times(1)).findByCodigo(destinoActualizar.getCodigo());
    }


    // <----------TEST ELIMINAR------->
    @Test
    void eliminarDestinoExistente(){
        when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));


        assertDoesNotThrow(() -> destinoServiceImpl.eliminar(1L));


        verify(destinoRepository, times(1)).findById(1L);
        verify(destinoRepository, times(1)).delete(destino);
    }
    @Test
    void eliminarDestinoNoExiste(){
        when(destinoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class,()->destinoServiceImpl.eliminar(1L));

        verify(destinoRepository, times(1)).findById(1L);
        verify(destinoRepository,never()).delete(destino);

    }
    //<------- TEST DE BUSCAR -------->
    @Test
    void listarTodos_Exitoso() {
        when(destinoRepository.findAll()).thenReturn(List.of(destino));

        List<Destino> lista = destinoServiceImpl.listarTodos();

        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("Jujuy", lista.get(0).getNombre());

        verify(destinoRepository, times(1)).findAll();
    }

    // <----------TEST BUSCAR POR ID------->
    @Test
    void buscarPorId_Exitoso() throws ElementoNoEncontradoException {
        when(destinoRepository.findById(1L)).thenReturn(Optional.of(destino));

        Destino encontrado = destinoServiceImpl.buscarPorID(1L);

        assertNotNull(encontrado);
        assertEquals(destino.getId(), encontrado.getId());
        assertEquals(destino.getNombre(), encontrado.getNombre());

        verify(destinoRepository, times(1)).findById(1L);
    }

    @Test
    void buscarPorId_NoExiste() {
        when(destinoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> destinoServiceImpl.buscarPorID(1L));

        verify(destinoRepository, times(1)).findById(1L);
    }

    // <----------TEST BUSCAR POR CODIGO------->
    @Test
    void buscarPorCodigo_Exitoso() throws ElementoNoEncontradoException {
        when(destinoRepository.findByCodigo(destino.getCodigo())).thenReturn(Optional.of(destino));

        Destino encontrado = destinoServiceImpl.buscarPorCodigo(destino.getCodigo());

        assertNotNull(encontrado);
        assertEquals(destino.getCodigo(), encontrado.getCodigo());

        verify(destinoRepository, times(1)).findByCodigo(destino.getCodigo());
    }

    @Test
    void buscarPorCodigo_NoExiste() {
        when(destinoRepository.findByCodigo("COD999")).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> destinoServiceImpl.buscarPorCodigo("COD999"));

        verify(destinoRepository, times(1)).findByCodigo("COD999");
    }

    // <----------TEST BUSCAR POR NOMBRE------->
    @Test
    void buscarPorNombre_Exitoso() throws ElementoNoEncontradoException {
        when(destinoRepository.findByNombre("jujuy")).thenReturn(Optional.of(List.of(destino)));

        List<Destino> lista = destinoServiceImpl.buscarPorNombre("Jujuy");

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Jujuy", lista.get(0).getNombre());

        verify(destinoRepository, times(1)).findByNombre("jujuy");
    }

    @Test
    void buscarPorNombre_NoExiste() {
        when(destinoRepository.findByNombre("salta")).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> destinoServiceImpl.buscarPorNombre("Salta"));

        verify(destinoRepository, times(1)).findByNombre("salta");
    }

    // <----------TEST BUSCAR POR PAIS------->
    @Test
    void buscarPorPais_Exitoso() throws ElementoNoEncontradoException {
        when(destinoRepository.findByPais("argentina")).thenReturn(Optional.of(List.of(destino)));

        List<Destino> lista = destinoServiceImpl.buscarPorPais("Argentina");

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Argentina", lista.get(0).getPais());

        verify(destinoRepository, times(1)).findByPais("argentina");
    }

    @Test
    void buscarPorPais_NoExiste() {
        when(destinoRepository.findByPais("brasil")).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> destinoServiceImpl.buscarPorPais("Brasil"));

        verify(destinoRepository, times(1)).findByPais("brasil");
    }


}