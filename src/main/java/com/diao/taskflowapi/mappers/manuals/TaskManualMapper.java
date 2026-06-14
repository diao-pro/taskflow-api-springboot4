package com.diao.taskflowapi.mappers.manuals;

import com.diao.taskflowapi.dtos.requests.TaskRequest;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.entities.Task;
import com.diao.taskflowapi.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Version "classique" du mapper Task.
 * <p>
 * Toute la logique de conversion est ecrite a la main : on voit
 * precisement comment chaque champ est rempli, y compris les
 * relations imbriquees (project, assignee).
 *
 * @see com.diao.taskflowapi.mappers.autos.TaskMapStructMapper version declarative equivalente
 */
@Component("taskManualMapper")
@RequiredArgsConstructor
public class TaskManualMapper {

    private final UserManualMapper userManualMapper;

    /**
     * Convertit une entite Task en DTO de reponse.
     */
    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee() != null ? userManualMapper.toResponse(task.getAssignee()) : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    /**
     * Cree une nouvelle entite Task a partir d'une requete de creation.
     * Le projet et l'assignee doivent etre resolus au prealable par le service
     * (car ce mapper n'a pas acces aux repositories).
     */
    public Task toEntity(TaskRequest request, Project project, User assignee) {
        return Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .project(project)
                .assignee(assignee)
                .build();
    }

    /**
     * Met a jour une entite existante avec les valeurs de la requete (update in place).
     */
    public void updateEntity(Task task, TaskRequest request, Project project, User assignee) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());
        task.setProject(project);
        task.setAssignee(assignee);
    }
}