package com.diao.taskflowapi.dtos.responses;

import com.diao.taskflowapi.enums.Role;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

/**
 * Vue publique d'un utilisateur (sans mot de passe).
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        @Nullable String jobTitle,
        Instant createdAt
) {
}