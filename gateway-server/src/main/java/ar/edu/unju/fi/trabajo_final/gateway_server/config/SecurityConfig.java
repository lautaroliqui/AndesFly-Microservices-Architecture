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
    @Value("${jwt.secret:ClaveSecretaDemoParaPortafolioGithub1234567890}")
    private String jwtSecret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.csrf(csrf -> csrf.disable());

        http.authorizeExchange(exchanges -> exchanges
                // 1. Rutas Públicas (Auth y Swagger)
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                .pathMatchers("/api/v1_1/auth/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1_1/vuelos/**", "/api/v1_1/destinos/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()

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
                .pathMatchers(HttpMethod.GET, "/api/v1_1/clientes/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/api/v1_1/cliente/**").authenticated()

                // 4. Rutas de Reservas
                .pathMatchers("/api/v1_1/reservas/**", "/api/v1_1/reserva/**").authenticated()

                .anyExchange().authenticated()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
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
            if (!rol.startsWith("ROLE_")) {
                rol = "ROLE_" + rol;
            }
            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(rol));
            return authorities;
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cachear la respuesta de preflight 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {

        if (jwtSecret == null || jwtSecret.length() < 32) {

            jwtSecret = "ClaveSecretaDemoParaPortafolioGithub1234567890";
        }
        byte[] keyBytes = jwtSecret.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}