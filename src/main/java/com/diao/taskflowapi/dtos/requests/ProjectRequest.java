package com.diao.taskflowapi.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/**
 * Donnees pour creer ou mettre a jour un projet.
 */
public record ProjectRequest(

        @NotBlank(message = "Le nom du projet est obligatoire")
        @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caracteres")
        String name,

        @Nullable
        @Size(max = 1000, message = "La description ne doit pas depasser 1000 caracteres")
        String description
) {
}