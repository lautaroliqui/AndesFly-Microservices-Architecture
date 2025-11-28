package ar.edu.unju.fi.trabajo_final.microservicio_cliente.service;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtil {

    // 1. Lee la clave secreta desde el .properties (Git)
    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 2. Genera el token
    public String generarToken(Cliente cliente) {
        Map<String, Object> claims = new HashMap<>();
        // 3. Añade los "claims" (datos) que el Gateway y otros servicios necesitarán
        claims.put("id", cliente.getId());
        claims.put("rol", cliente.getRol());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(cliente.getEmail()) // 'sub' (Subject) es el email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // (Añadiríamos métodos 'validarToken' y 'getClaims' para el Gateway,
    // pero el Cliente solo necesita 'generarToken')
}