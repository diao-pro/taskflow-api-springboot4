package com.diao.taskflowapi.controllers;

import com.diao.taskflowapi.dtos.requests.TaskRequest;
import com.diao.taskflowapi.dtos.requests.TaskStatusUpdateRequest;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.enums.Role;
import com.diao.taskflowapi.securities.CustomUserDetails;
import com.diao.taskflowapi.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de gestion des taches.
 * <p>
 * Demonstration du <b>versioning d'API natif de Spring Boot 4</b> :
 * l'endpoint de detail d'une tache existe en version "1.0" (legacy) et "2.0"
 * (avec champ supplementaire), routees via {@code @ApiVersion} et la
 * configuration definie dans {@link com.diao.taskflowapi.config.ApiVersioningConfig}.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Taches", description = "Gestion des taches au sein des projets")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Creer une tache")
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TaskResponse response = taskService.create(request, principal.getId(), isAdmin(principal));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister les taches d'un projet",
            description = "Necessite le parametre projectId. Retourne une page de taches.")
    public ResponseEntity<Page<TaskResponse>> findByProject(
            @RequestParam Long projectId,
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable
    ) {
        Page<TaskResponse> page = taskService.findByProject(projectId, principal.getId(), isAdmin(principal), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/me")
    @Operation(summary = "Mes taches", description = "Retourne les taches assignees a l'utilisateur connecte")
    public ResponseEntity<Page<TaskResponse>> findMyTasks(
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable
    ) {
        Page<TaskResponse> page = taskService.findMyTasks(principal.getId(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une tache (v1)",
            description = "Version par defaut / legacy de l'endpoint de detail")
    public ResponseEntity<TaskResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TaskResponse response = taskService.findById(id, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}", version = "2")
    @Operation(summary = "Detail d'une tache (v2)",
            description = "Version 2 de l'API : meme contenu, reservee aux clients qui demandent "
                    + "explicitement la version 2 (ex: header ou path selon la config de versioning). "
                    + "Demonstration du versioning natif de Spring Boot 4 / Spring Framework 7.")
    public ResponseEntity<TaskResponse> findByIdV2(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        // Dans une vraie v2, on pourrait retourner un DTO enrichi (TaskResponseV2).
        // Ici, pour rester simple, on reutilise le meme service/DTO.
        TaskResponse response = taskService.findById(id, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une tache (tous les champs)")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TaskResponse response = taskService.update(id, request, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Changer le statut d'une tache",
            description = "Endpoint leger, utile pour un tableau Kanban (drag & drop)")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        TaskResponse response = taskService.updateStatus(id, request, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une tache")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        taskService.delete(id, principal.getId(), isAdmin(principal));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(CustomUserDetails principal) {
        User user = principal.getUser();
        return user.getRole() == Role.ROLE_ADMIN;
    }
}