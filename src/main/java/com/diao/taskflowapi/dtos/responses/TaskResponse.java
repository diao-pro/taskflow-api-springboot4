package com.diao.taskflowapi.dtos.responses;

import com.diao.taskflowapi.enums.TaskPriority;
import com.diao.taskflowapi.enums.TaskStatus;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Vue d'une tache.
 */
public record TaskResponse(
        Long id,
        String title,
        @Nullable String description,
        TaskStatus status,
        TaskPriority priority,
        @Nullable LocalDate dueDate,
        Long projectId,
        String projectName,
        @Nullable UserResponse assignee,
        Instant createdAt,
        Instant updatedAt
) {
}