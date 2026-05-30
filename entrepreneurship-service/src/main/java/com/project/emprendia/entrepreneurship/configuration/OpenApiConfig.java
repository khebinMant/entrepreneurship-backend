package com.project.emprendia.entrepreneurship.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Entrepreneurship Service API")
                .version("v1.1.0")
                .description("API para gestión completa de emprendimientos: datos principales, ubicaciones físicas, redes sociales y portales web personalizados")
                .contact(new Contact()
                    .name("Kevin Guachagmira")
                    .email("kguachag@pichincha.com")))
            .servers(List.of(
                new Server().url("http://localhost:8082").description("Local Development Server"),
                new Server().url("https://api.emprendia.com").description("Production Server")
            ));
    }
}

