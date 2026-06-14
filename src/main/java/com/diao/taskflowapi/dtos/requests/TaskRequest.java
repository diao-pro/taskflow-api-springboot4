package com.diao.taskflowapi.dtos.requests;

import com.diao.taskflowapi.enums.TaskPriority;
import com.diao.taskflowapi.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/**
 * Donnees pour creer ou mettre a jour une tache.
 */
public record TaskRequest(

        @NotBlank(message = "Le titre est obligatoire")
        @Size(min = 2, max = 200, message = "Le titre doit contenir entre 2 et 200 caracteres")
        String title,

        @Nullable
        @Size(max = 2000, message = "La description ne doit pas depasser 2000 caracteres")
        String description,

        @NotNull(message = "Le statut est obligatoire")
        TaskStatus status,

        @NotNull(message = "La priorite est obligatoire")
        TaskPriority priority,

        @Nullable
        LocalDate dueDate,

        @NotNull(message = "L'identifiant du projet est obligatoire")
        Long projectId,

        /**
         * Identifiant de l'utilisateur assigne. Null = non assignee.
         */
        @Nullable
        Long assigneeId
) {
}