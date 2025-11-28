package ar.edu.unju.fi.trabajo_final.gateway_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.cors(Customizer.withDefaults());

        http.csrf(csrf -> csrf.disable());

        http.authorizeExchange(exchanges -> exchanges
                // 1. Rutas Públicas
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                .pathMatchers("/api/v1_1/auth/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1_1/vuelos/**", "/api/v1_1/destinos/**").permitAll()

                // 2. Rutas de Vuelo (Solo ADMIN puede modificar)
                .pathMatchers(HttpMethod.PUT, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/v1_1/vuelo/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/v1_1/destino/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/v1_1/destino/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/v1_1/destino/**").hasRole("ADMIN")
                // (Permitimos GET /vuelo/{id} para usuarios autenticados)
                .pathMatchers(HttpMethod.GET, "/api/v1_1/vuelo/**", "/api/v1_1/destino/**").authenticated()

                // 3. Rutas de Cliente (Solo ADMIN puede modificar)
                .pathMatchers(HttpMethod.PUT, "/api/v1_1/cliente/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/v1_1/cliente/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/v1_1/cliente/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/api/v1_1/clientes/**").hasRole("ADMIN") // Listado completo solo ADMIN
                // (Permitimos GET /cliente/{id} para usuarios autenticados)
                .pathMatchers(HttpMethod.GET, "/api/v1_1/cliente/**").authenticated()

                // 4. Rutas de Reservas
                .pathMatchers("/api/v1_1/reservas/**", "/api/v1_1/reserva/**").authenticated()

                .anyExchange().authenticated()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                // 2. Le decimos que use nuestro decodificador manual (con la clave secreta)
                //    y nuestro extractor de roles
                jwt.jwtDecoder(reactiveJwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
        ));

        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            if (rol == null) {
                return Collections.emptyList();
            }
            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));
            return authorities;
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    // 2. Define la configuración de CORS (Evita errores en Front-end)
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite peticiones desde el frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        // Permite los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite todas las cabeceras (incluyendo 'Authorization' para el token)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta configuración a TODAS las rutas
        return source;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}