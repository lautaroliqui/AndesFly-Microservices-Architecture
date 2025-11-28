package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 1. RUTAS PÚBLICAS (Todos pueden buscar vuelos)
                        .requestMatchers(HttpMethod.GET, "/api/v1_1/vuelos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1_1/destinos/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs/swagger-config",
                                "/webjars/**"
                        ).permitAll()

                        // 2. RUTAS DE ADMIN (Solo ADMIN puede crear/editar/borrar vuelos)
                        .requestMatchers(HttpMethod.POST, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1_1/destino/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1_1/destino/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1_1/destino/**").hasRole("ADMIN")

                        // 3. RUTAS AUTENTICADAS (Cualquier usuario logueado puede ver un vuelo/destino específico)
                        // (Esto permite al ms-reserva llamar a obtenerVueloPorId)
                        .requestMatchers(HttpMethod.GET, "/api/v1_1/vuelo/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1_1/destino/**").authenticated()

                        .anyRequest().authenticated()
                )
                // 4. Configura la validación de JWT
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

        return http.build();
    }

    // Bean para enseñar a Spring a leer el claim "rol"
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            if (rol == null) {
                return Collections.emptyList();
            }
            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));
            return authorities;
        });
        return converter;
    }

    // Bean para enseñar a Spring a validar con la clave secreta
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}