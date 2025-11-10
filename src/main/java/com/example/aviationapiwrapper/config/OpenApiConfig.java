package com.example.aviationapiwrapper.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
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
    public OpenAPI aviationApiOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Aviation API Wrapper").version("1.0.0")
                                .description("Microservice wrapping AviationAPI to provide typed airport data.")
                                .contact(new Contact().name("Álvaro Gutiérrez").email("alvaro.glopez@gmail.com")))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local")))
                .externalDocs(new ExternalDocumentation().description("AviationAPI Docs").url("https://docs.aviationapi.com/#tag/airports"));
    }
}
