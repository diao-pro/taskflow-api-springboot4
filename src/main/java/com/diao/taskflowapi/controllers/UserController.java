package com.diao.taskflowapi.controllers;

import com.diao.taskflowapi.dtos.responses.UserResponse;
import com.diao.taskflowapi.mappers.autos.UserMapStructMapper;
import com.diao.taskflowapi.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints utilitaires lies aux utilisateurs.
 * <p>
 * Permet par exemple de lister les utilisateurs disponibles pour
 * assigner une tache (champ {@code assigneeId} de {@link com.diao.taskflowapi.dto.request.TaskRequest}).
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Consultation des utilisateurs")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapStructMapper userMapper;

    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs",
            description = "Utile pour choisir un assignee lors de la creation d'une tache")
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }
}