package net.engineeringdigest.journalApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customSwagger(){
        return new OpenAPI()
                .info(
                    new Info().title("Journal Application APIs")
                            .description("by halwai")
                )
                .servers(
                    Arrays.asList(
                        new Server().url("localhost:8080").description("local"),
                        new Server().url("localhost:8081").description("live")
                    )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(
                        new Components().addSecuritySchemes(
                        "bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                );
    }

}
