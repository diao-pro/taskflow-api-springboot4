package com.diao.taskflowapi.services.impls;

import com.diao.taskflowapi.dtos.requests.TaskRequest;
import com.diao.taskflowapi.dtos.requests.TaskStatusUpdateRequest;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.entities.Task;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.exceptions.AccessDeniedToResourceException;
import com.diao.taskflowapi.exceptions.ResourceNotFoundException;
import com.diao.taskflowapi.mappers.autos.TaskMapStructMapper;
import com.diao.taskflowapi.repositories.ProjectRepository;
import com.diao.taskflowapi.repositories.TaskRepository;
import com.diao.taskflowapi.repositories.UserRepository;
import com.diao.taskflowapi.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation des operations metier sur les taches.
 * <p>
 * Utilise le mapper MAPSTRUCT ({@link TaskMapStructMapper}) a titre de
 * demonstration / comparaison avec le mapper manuel utilise pour les projets
 * (voir {@link com.diao.taskflowapi.services.impls.ProjectServiceImpl}).
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapStructMapper taskMapper;

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request, Long currentUserId, boolean isAdmin) {
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> ResourceNotFoundException.of("Projet", request.projectId()));

        checkProjectOwnershipOrAdmin(project, currentUserId, isAdmin);

        User assignee = resolveAssignee(request.assigneeId());

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .project(project)
                .assignee(assignee)
                .build();

        Task saved = taskRepository.save(task);

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> findByProject(Long projectId, Long currentUserId, boolean isAdmin, Pageable pageable) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> ResourceNotFoundException.of("Projet", projectId));

        checkProjectOwnershipOrAdmin(project, currentUserId, isAdmin);

        return taskRepository.findByProjectId(projectId, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse findById(Long taskId, Long currentUserId, boolean isAdmin) {
        Task task = getTaskOrThrow(taskId);
        checkProjectOwnershipOrAdmin(task.getProject(), currentUserId, isAdmin);

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse update(Long taskId, TaskRequest request, Long currentUserId, boolean isAdmin) {
        Task task = getTaskOrThrow(taskId);
        checkProjectOwnershipOrAdmin(task.getProject(), currentUserId, isAdmin);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> ResourceNotFoundException.of("Projet", request.projectId()));
        checkProjectOwnershipOrAdmin(project, currentUserId, isAdmin);

        User assignee = resolveAssignee(request.assigneeId());

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setProject(project);
        task.setAssignee(assignee);

        Task saved = taskRepository.save(task);

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TaskResponse updateStatus(Long taskId, TaskStatusUpdateRequest request, Long currentUserId, boolean isAdmin) {
        Task task = getTaskOrThrow(taskId);
        checkProjectOwnershipOrAdmin(task.getProject(), currentUserId, isAdmin);

        task.setStatus(request.status());
        Task saved = taskRepository.save(task);

        return taskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long taskId, Long currentUserId, boolean isAdmin) {
        Task task = getTaskOrThrow(taskId);
        checkProjectOwnershipOrAdmin(task.getProject(), currentUserId, isAdmin);

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> findMyTasks(Long assigneeId, Pageable pageable) {
        return taskRepository.findByAssigneeId(assigneeId, pageable)
                .map(taskMapper::toResponse);
    }

    // ===================== Helpers =====================

    private Task getTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> ResourceNotFoundException.of("Tache", taskId));
    }

    /**
     * Resout l'utilisateur assigne a partir de son id, ou retourne null
     * si aucun id n'est fourni (tache non assignee).
     */
    private @Nullable User resolveAssignee(@Nullable Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> ResourceNotFoundException.of("Utilisateur (assigne)", assigneeId));
    }

    /**
     * Verifie que l'utilisateur courant est proprietaire du projet
     * auquel appartient la tache, ou administrateur.
     */
    private void checkProjectOwnershipOrAdmin(Project project, Long currentUserId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        if (!project.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedToResourceException(
                    "Vous n'etes pas autorise a gerer les taches de ce projet"
            );
        }
    }
}