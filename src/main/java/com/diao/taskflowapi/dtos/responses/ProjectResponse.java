package com.diao.taskflowapi.dtos.responses;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * Vue d'un projet, avec quelques infos de synthese.
 */
public record ProjectResponse(
        Long id,
        String name,
        @Nullable String description,
        UserResponse owner,
        int totalTasks,
        int completedTasks,
        Instant createdAt,
        @Nullable List<TaskResponse> tasks
) {
}