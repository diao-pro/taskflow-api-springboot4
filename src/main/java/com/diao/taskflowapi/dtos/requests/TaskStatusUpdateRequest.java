package com.diao.taskflowapi.dtos.requests;

import com.diao.taskflowapi.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Requete legere pour ne mettre a jour que le statut d'une tache
 * (utilisee par exemple pour un drag & drop de type Kanban).
 */
public record TaskStatusUpdateRequest(

        @NotNull(message = "Le nouveau statut est obligatoire")
        TaskStatus status
) {
}