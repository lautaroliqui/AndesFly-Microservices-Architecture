package ar.edu.unju.fi.trabajo_final.microservicio_cliente;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.repository.ClienteRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.impl.ClienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @InjectMocks
    ClienteServiceImpl clienteService;

    private Cliente clienteNuevo;

    @BeforeEach
    void setUp() {
        clienteNuevo = new Cliente(
                null,
                "Lionel Messi",
                "leo.messi@mail.com",
                "30000000",
                new ArrayList<>(),
                "123456789",
                "ROLE_USER"
        );
    }

    // -------------------------------------------------------------------------
    // --- TEST GUARDAR ---
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("G-01: Debe guardar un cliente si email y documento son únicos")
    void testGuardar_UnicidadExitosa() throws ValidacionException {

        when(clienteRepository.findByEmail(anyString())).thenReturn(null);
        when(clienteRepository.findByDocumento(anyString())).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(
                new Cliente(1L, "Lionel Messi", "leo.messi@mail.com", "30000000", new ArrayList<>(),"123456789","ROLE_USER")
        );

        Cliente clienteGuardado = clienteService.guardar(clienteNuevo);

        assertNotNull(clienteGuardado.getId());
        verify(clienteRepository).save(clienteNuevo);
    }

    @Test
    @DisplayName("G-02: Debe lanzar ValidacionException si el email ya existe")
    void testGuardar_EmailDuplicado_LanzaExcepcion() {

        Cliente existente = new Cliente(2L, "Otro", clienteNuevo.getEmail(), "444", new ArrayList<>(),"123","ROLE_USER");
        when(clienteRepository.findByEmail(clienteNuevo.getEmail())).thenReturn(existente);

        assertThrows(ValidacionException.class, () -> clienteService.guardar(clienteNuevo));

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("G-03: Debe lanzar ValidacionException si el documento ya existe")
    void testGuardar_DocumentoDuplicado_LanzaExcepcion() {

        when(clienteRepository.findByEmail(anyString())).thenReturn(null);

        Cliente existente = new Cliente(3L, "X", "otro@mail.com", clienteNuevo.getDocumento(), new ArrayList<>(),"123","ROLE_USER");
        when(clienteRepository.findByDocumento(clienteNuevo.getDocumento())).thenReturn(existente);

        assertThrows(ValidacionException.class, () -> clienteService.guardar(clienteNuevo));
    }

    // -------------------------------------------------------------------------
    // --- TEST ACTUALIZAR ---
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("A-01: Debe actualizar el cliente si existe y los datos son únicos")
    void testActualizar_Exitosa() throws ValidacionException, ElementoNoEncontradoException {

        Cliente clienteExistente = new Cliente(
                1L, "Lionel Messi", "leo.messi@mail.com", "30000000",
                new ArrayList<>(), "pass", "ROLE_USER"
        );

        Cliente clienteActualizado = new Cliente(
                1L, "Lionel Andres Messi", "leo.messi.nuevo@mail.com", "30000000",
                new ArrayList<>(), "pass", "ROLE_USER"
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepository.findByEmail(anyString())).thenReturn(null);
        when(clienteRepository.findByDocumento(anyString())).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteExistente);

        Cliente resultado = clienteService.actualizar(clienteActualizado);

        assertEquals("Lionel Andres Messi", resultado.getNombreCompleto());
        assertEquals("leo.messi.nuevo@mail.com", resultado.getEmail());

        verify(clienteRepository).save(clienteExistente);
    }

    @Test
    @DisplayName("A-02: Debe fallar si el cliente a actualizar no existe")
    void testActualizar_ClienteNoEncontrado() {

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        clienteNuevo.setId(99L);

        assertThrows(ElementoNoEncontradoException.class, () -> clienteService.actualizar(clienteNuevo));

        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("A-03: Debe fallar si el email ya pertenece a otro cliente")
    void testActualizar_EmailDuplicadoDeOtro() {

        Cliente existente = new Cliente(
                1L, "Propio", "propio@mail.com", "111",
                new ArrayList<>(),"123","ROLE_USER");

        Cliente otro = new Cliente(
                2L, "Otro", "duplicado@mail.com", "222",
                new ArrayList<>(),"123","ROLE_USER");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepository.findByEmail("duplicado@mail.com")).thenReturn(otro);

        Cliente datosUpdate = new Cliente(
                1L, "Propio Mod", "duplicado@mail.com", "111",
                new ArrayList<>(),"123","ROLE_USER");

        assertThrows(ValidacionException.class, () -> clienteService.actualizar(datosUpdate));

        verify(clienteRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // --- TEST CRUD BÁSICOS ---
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("C-01: Debe retornar la lista de todos los clientes")
    void testBuscarTodos() {

        List<Cliente> lista = Arrays.asList(
                new Cliente(1L,"A","a@a.com","1",new ArrayList<>(),"1","ROLE_USER"),
                new Cliente(2L,"B","b@b.com","2",new ArrayList<>(),"2","ROLE_USER")
        );

        when(clienteRepository.findAll()).thenReturn(lista);

        List<Cliente> resultado = clienteService.buscarTodos();

        assertEquals(2, resultado.size());
        assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("C-02: Debe retornar el cliente si existe")
    void testBuscarPorId_Existente() throws ElementoNoEncontradoException {

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteNuevo));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Lionel Messi", resultado.getNombreCompleto());
    }

    @Test
    @DisplayName("C-02b: Debe lanzar ElementoNoEncontradoException si no existe")
    void testBuscarPorId_NoExistente_LanzaExcepcion() {

        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> clienteService.buscarPorId(99L));
    }

    @Test
    @DisplayName("C-03: Debe eliminar si el cliente existe")
    void testEliminar_Exitosa() throws ElementoNoEncontradoException {

        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.eliminar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("C-04: Debe lanzar excepción al intentar eliminar cliente inexistente")
    void testEliminar_ClienteNoEncontrado() {

        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(ElementoNoEncontradoException.class, () -> clienteService.eliminar(99L));

        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
