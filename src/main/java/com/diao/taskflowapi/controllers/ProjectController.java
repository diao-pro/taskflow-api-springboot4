package com.diao.taskflowapi.controllers;

import com.diao.taskflowapi.dtos.requests.ProjectRequest;
import com.diao.taskflowapi.dtos.responses.ProjectResponse;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.enums.Role;
import com.diao.taskflowapi.securities.CustomUserDetails;
import com.diao.taskflowapi.services.ProjectService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de gestion des projets.
 * <p>
 * Tous necessitent un token JWT valide. Un utilisateur ne voit/modifie
 * que ses propres projets, sauf s'il a le role {@link Role#ROLE_ADMIN}.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projets", description = "Gestion des projets")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Creer un projet", description = "Cree un nouveau projet appartenant a l'utilisateur connecte")
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ProjectResponse response = projectService.create(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister mes projets", description = "Retourne la liste paginee des projets de l'utilisateur connecte")
    public ResponseEntity<Page<ProjectResponse>> findMyProjects(
            @AuthenticationPrincipal CustomUserDetails principal,
            Pageable pageable
    ) {
        Page<ProjectResponse> page = projectService.findMyProjects(principal.getId(), pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'un projet", description = "Retourne un projet avec la liste de ses taches")
    public ResponseEntity<ProjectResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ProjectResponse response = projectService.findById(id, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour un projet")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ProjectResponse response = projectService.update(id, request, principal.getId(), isAdmin(principal));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un projet", description = "Supprime le projet ainsi que toutes ses taches")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        projectService.delete(id, principal.getId(), isAdmin(principal));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(CustomUserDetails principal) {
        User user = principal.getUser();
        return user.getRole() == Role.ROLE_ADMIN;
    }
}