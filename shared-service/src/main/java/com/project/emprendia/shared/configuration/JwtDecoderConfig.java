package com.project.emprendia.shared.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtDecoderConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI:}") String jwksUri,
            @Value("${SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI:http://localhost:8080/realms/emprendia}") String issuerUri) throws Exception {
        NimbusJwtDecoder decoder;
        if (!jwksUri.isBlank()) {
            decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
            decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
        } else {
            decoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        }
        return decoder;
    }
}