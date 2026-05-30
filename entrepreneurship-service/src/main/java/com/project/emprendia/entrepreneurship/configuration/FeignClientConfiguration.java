package com.project.emprendia.entrepreneurship.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Configuración de Feign Client para propagar token JWT
 */
@Slf4j
@Configuration
public class FeignClientConfiguration {

    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                    Jwt jwt = jwtAuth.getToken();
                    String tokenValue = jwt.getTokenValue();

                    log.debug("Propagando token JWT a Feign Client");
                    requestTemplate.header("Authorization", "Bearer " + tokenValue);
                } else {
                    log.debug("No hay autenticación JWT disponible para propagar");
                }
            }
        };
    }
}

