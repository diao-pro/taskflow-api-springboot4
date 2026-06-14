package com.diao.taskflowapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure le <b>versioning d'API natif</b> introduit par Spring Framework 7
 * (Spring Boot 4).
 * <p>
 * Strategie choisie ici : versioning via un en-tete HTTP {@code X-API-Version}.
 * <ul>
 *     <li>Si l'en-tete est absent -> version par defaut (1) -> methode sans {@code version}</li>
 *     <li>Si l'en-tete vaut "2" -> route vers la methode annotee {@code version = "2"}</li>
 * </ul>
 * <p>
 * Exemple d'appel v2 :
 * <pre>
 * GET /api/v1/tasks/42
 * X-API-Version: 2
 * </pre>
 *
 * D'autres strategies sont possibles (path segment, query param, media type) :
 * voir {@code configurer.usePathSegment(...)}, {@code .useQueryParam(...)},
 * {@code .useMediaTypeParameter(...)}.
 */
@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {

    private static final String VERSION_HEADER = "X-API-Version";

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader(VERSION_HEADER)
                .addSupportedVersions("1", "2")
                .setDefaultVersion("1");
    }
}