package com.example.TFG_WebApp.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
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
                        .title("Club Members API")
                        .version("1.0")
                        .description("Documentación de la API REST para la gestión de miembros y entrenadores del club"))
                .servers(List.of(new Server().url("https://localhost:443").description("Servidor local HTTPS")
                ));
    }
}
