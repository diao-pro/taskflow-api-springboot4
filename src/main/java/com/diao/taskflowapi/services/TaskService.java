package com.diao.taskflowapi.services;

import com.diao.taskflowapi.dtos.requests.TaskRequest;
import com.diao.taskflowapi.dtos.requests.TaskStatusUpdateRequest;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Operations metier liees aux taches.
 */
public interface TaskService {

    /**
     * Cree une nouvelle tache au sein d'un projet.
     *
     * @throws com.diao.taskflowapi.exceptions.ResourceNotFoundException si le projet ou l'assignee n'existe pas
     * @throws com.diao.taskflowapi.exceptions.AccessDeniedToResourceException si l'utilisateur n'est pas proprietaire du projet
     */
    TaskResponse create(TaskRequest request, Long currentUserId, boolean isAdmin);

    /**
     * Retourne les taches d'un projet, paginees.
     */
    Page<TaskResponse> findByProject(Long projectId, Long currentUserId, boolean isAdmin, Pageable pageable);

    /**
     * Retourne le detail d'une tache.
     */
    TaskResponse findById(Long taskId, Long currentUserId, boolean isAdmin);

    /**
     * Met a jour une tache existante (tous les champs).
     */
    TaskResponse update(Long taskId, TaskRequest request, Long currentUserId, boolean isAdmin);

    /**
     * Met a jour uniquement le statut d'une tache (ex: drag & drop kanban).
     */
    TaskResponse updateStatus(Long taskId, TaskStatusUpdateRequest request, Long currentUserId, boolean isAdmin);

    /**
     * Supprime une tache.
     */
    void delete(Long taskId, Long currentUserId, boolean isAdmin);

    /**
     * Retourne les taches assignees a un utilisateur donne ("Mes taches").
     */
    Page<TaskResponse> findMyTasks(Long assigneeId, Pageable pageable);
}