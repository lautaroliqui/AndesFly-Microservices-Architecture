package ar.edu.unju.fi.trabajo_final.microservicio_reserva.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        // 1. Obtiene la petición HTTP actual
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            // 2. Obtiene el token "Authorization" (Bearer eyJhbG...) de la petición actual
            String authorizationHeader = attributes.getRequest().getHeader(AUTHORIZATION_HEADER);

            if (authorizationHeader != null) {
                // 3. Lo añade a la plantilla de la petición Feign saliente
                template.header(AUTHORIZATION_HEADER, authorizationHeader);
            }
        }
    }
}