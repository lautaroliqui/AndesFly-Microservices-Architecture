package ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_DeberiaRetornarUserDetailsCuandoExiste() {
        Cliente cliente = new Cliente();
        cliente.setEmail("usuario@mail.com");
        cliente.setPassword("12345");
        cliente.setRol("ROLE_USER");

        when(clienteRepository.findByEmail("usuario@mail.com"))
                .thenReturn(cliente);

        UserDetails userDetails = userDetailsService.loadUserByUsername("usuario@mail.com");

        assertNotNull(userDetails);
        assertEquals("usuario@mail.com", userDetails.getUsername());
        assertEquals("12345", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))
        );

        verify(clienteRepository, times(1)).findByEmail("usuario@mail.com");
    }

    @Test
    void loadUserByUsername_DeberiaLanzarExcepcionSiNoExiste() {
        when(clienteRepository.findByEmail("noexiste@mail.com"))
                .thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("noexiste@mail.com")
        );

        verify(clienteRepository, times(1)).findByEmail("noexiste@mail.com");
    }
}
