package com.diao.taskflowapi.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
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

    /**
     * Personnalise OpenAPI pour injecter l'en-tête X-API-Version globalement
     * sur chaque endpoint de la documentation Swagger UI.
     */
    /*@Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> {
            // Création de la définition du paramètre d'en-tête
            Parameter versionHeader = new Parameter()
                    .in("header")
                    .name("X-API-Version")
                    .description("Version de l'API cible (1 ou 2)")
                    .required(false) // Optionnel car géré par setDefaultVersion("1") côté Spring
                    .schema(new StringSchema()._default("1")); // Valeur par défaut dans Swagger

            // Injection du paramètre dans toutes les opérations de toutes les routes
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation ->
                            operation.addParametersItem(versionHeader)
                    )
            );
        };
    }*/

    /**
     * Configure et fournit un bean GroupedOpenApi pour la version 1 de l'API.
     * Cette configuration regroupe les points de terminaison sous '/api/v1/**' et les étiquette
     * comme « Version 1 (Legacy) » dans la documentation de l'API.
     *
     * @return une instance GroupedOpenApi configurée pour la version 1 de l'API.
     */
    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("Version 1 (Legacy)")
                .pathsToMatch("/api/v1/**")
                // On force l'injection du header personnalisé pour ce groupe
                .addOpenApiCustomizer(getVersionHeaderCustomizer("1"))
                // Filtre : On garde la méthode si la version est vide ou si elle vaut "1"
                .addOpenApiMethodFilter(method -> {
                    org.springframework.web.bind.annotation.RequestMapping requestMapping =
                            org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
                    if (requestMapping == null) return true;
                    String version = requestMapping.version();
                    return version.isEmpty() || version.equals("1");
                })
                .build();
    }

    /**
     * Configure et fournit un bean {@link GroupedOpenApi} pour l'API version 2.
     * Cette configuration regroupe les points de terminaison sous '/api/v1/**' et les étiquette
     * comme « Version 2 (Nouvelle) » dans la documentation de l'API.
     *
     * @return une instance de {@link GroupedOpenApi} configurée pour la version 2 de l'API.
     */
    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("Version 2 (Nouvelle)")
                .pathsToMatch("/api/v1/**")
                // Correction : On force l'injection du header personnalisé pour ce groupe avec "2" par défaut
                .addOpenApiCustomizer(getVersionHeaderCustomizer("2"))
                // Filtre : On garde la méthode uniquement si la version vaut explicitement "2"
                .addOpenApiMethodFilter(method -> {
                    org.springframework.web.bind.annotation.RequestMapping requestMapping =
                            org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation(method, org.springframework.web.bind.annotation.RequestMapping.class);
                    if (requestMapping == null) return false;
                    String version = requestMapping.version();
                    return version.equals("2");
                })
                .build();
    }




    /**
     * Logique réutilisable pour injecter l'en-tête X-API-Version dans un contexte OpenAPI.
     */
    private OpenApiCustomizer getVersionHeaderCustomizer(String defaultVersion) {
        return openApi -> {
            if (openApi.getPaths() != null) {
                // On parcourt chaque route (ex: "/api/v1/tasks/{id}", "/api/v1/projects", etc.)
                openApi.getPaths().forEach((path, pathItem) -> {
                    // CONDITION : On cible uniquement la route qui se termine par /tasks/{id}
                    if (path.endsWith("/tasks/{id}")) {
                        pathItem.readOperations().forEach(operation -> {
                            Parameter versionHeader = new Parameter()
                                    .in("header")
                                    .name("X-API-Version")
                                    .description("Version de l'API cible (1 ou 2)")
                                    .required(false)
                                    .schema(new StringSchema()._default(defaultVersion));

                            operation.addParametersItem(versionHeader);
                        });
                    }
                });
            }
        };
    }
}
