package com.diao.taskflowapi.services;

import com.diao.taskflowapi.dtos.requests.ProjectRequest;
import com.diao.taskflowapi.dtos.responses.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Operations metier liees aux projets.
 * <p>
 * La notion d'utilisateur courant est passee sous forme d'id (ownerId),
 * extrait du contexte de securite par le controller.
 */
public interface ProjectService {

    /**
     * Cree un nouveau projet appartenant a l'utilisateur donne.
     */
    ProjectResponse create(ProjectRequest request, Long ownerId);

    /**
     * Retourne la liste paginee des projets appartenant a l'utilisateur,
     * sous forme de vue resumee (sans le detail des taches).
     */
    Page<ProjectResponse> findMyProjects(Long ownerId, Pageable pageable);

    /**
     * Retourne le detail d'un projet (avec ses taches), si l'utilisateur
     * en est proprietaire ou est administrateur.
     *
     * @throws com.diao.taskflowapi.exceptions.ResourceNotFoundException si le projet n'existe pas
     * @throws com.diao.taskflowapi.exceptions.AccessDeniedToResourceException si l'utilisateur n'a pas le droit
     */
    ProjectResponse findById(Long projectId, Long currentUserId, boolean isAdmin);

    /**
     * Met a jour un projet existant.
     */
    ProjectResponse update(Long projectId, ProjectRequest request, Long currentUserId, boolean isAdmin);

    /**
     * Supprime un projet (et ses taches, via cascade).
     */
    void delete(Long projectId, Long currentUserId, boolean isAdmin);
}