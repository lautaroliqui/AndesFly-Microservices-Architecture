package ar.edu.unju.fi.trabajo_final.microservicio_cliente.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.ClienteService;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.service.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// DTO simple para recibir el login
record LoginRequest(String email, String password) {}

// DTO simple para enviar el token
record LoginResponse(String token) {}

@RestController
@RequestMapping("/api/v1_1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          ClienteService clienteService,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Cliente cliente = clienteService.buscarPorEmail(loginRequest.email());

        String token = jwtUtil.generarToken(cliente);

        return new ResponseEntity<>(new LoginResponse(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Cliente cliente) {

        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));
        cliente.setRol("ROLE_USER");
        clienteService.guardar(cliente);

        return new ResponseEntity<>(Map.of("mensaje", "Usuario registrado exitosamente"), HttpStatus.CREATED);
    }
}