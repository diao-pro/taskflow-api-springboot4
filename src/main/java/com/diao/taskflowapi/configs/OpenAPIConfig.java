package com.diao.taskflowapi.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentation - Taskflow")
                        .version("1.0")
                        .description("""
                            NB: Documentation de l'API avec gestion des utilisateurs, rôles et sécurité JWT (Authentification).  
                            
                            🔑 Authentification :  
                            - Endpoint `/api/v1/auth/login`  
                            - Format attendu : `application/x-www-form-urlencoded`  
                            - Champs :  
                                - `email` : votre identifiant  
                                - `password` : votre mot de passe  
                            - Retour : access_token  
                            """))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat("JWT")));
    }
}
