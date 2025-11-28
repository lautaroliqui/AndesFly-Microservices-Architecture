package ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Este método es llamado por Spring Security para cargar un usuario
     * (en nuestro caso, por su email).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Busca el cliente en la base de datos por su email
        Cliente cliente = clienteRepository.findByEmail(email);

        if (cliente == null) {
            throw new UsernameNotFoundException("No se encontró un cliente con el email: " + email);
        }

        // 2. Convierte el rol (ej. "ROLE_ADMIN") en una "Autoridad" de Spring
        Collection<? extends GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority(cliente.getRol()));

        // 3. Devuelve un objeto "User" de Spring Security
        // Spring comparará la contraseña de este objeto con la que viene en la petición.
        return new User(
                cliente.getEmail(),
                cliente.getPassword(),
                authorities // La colección de roles/autoridades
        );
    }
}