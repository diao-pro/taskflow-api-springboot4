package com.diao.taskflowapi.mappers.manuals;

import com.diao.taskflowapi.dtos.requests.ProjectRequest;
import com.diao.taskflowapi.dtos.responses.ProjectResponse;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Version "classique" du mapper Project.
 *
 * @see com.diao.taskflowapi.mappers.autos.ProjectMapStructMapper version declarative equivalente
 */
@Component("projectManualMapper")
@RequiredArgsConstructor
public class ProjectManualMapper {

    private final UserManualMapper userManualMapper;
    private final TaskManualMapper taskManualMapper;

    /**
     * Convertit un projet en DTO de synthese (sans la liste detaillee des taches).
     */
    public ProjectResponse toSummaryResponse(Project project) {
        if (project == null) {
            return null;
        }

        long total = project.getTasks().size();
        long done = project.getTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                userManualMapper.toResponse(project.getOwner()),
                (int) total,
                (int) done,
                project.getCreatedAt(),
                null // pas de detail des taches dans la vue resumee
        );
    }

    /**
     * Convertit un projet en DTO detaille, incluant la liste complete des taches.
     */
    public ProjectResponse toDetailedResponse(Project project) {
        if (project == null) {
            return null;
        }

        ProjectResponse summary = toSummaryResponse(project);

        List<TaskResponse> tasks = project.getTasks().stream()
                .map(taskManualMapper::toResponse)
                .toList();

        return new ProjectResponse(
                summary.id(),
                summary.name(),
                summary.description(),
                summary.owner(),
                summary.totalTasks(),
                summary.completedTasks(),
                summary.createdAt(),
                tasks
        );
    }

    /**
     * Cree une nouvelle entite Project (le owner est resolu par le service).
     */
    public Project toEntity(ProjectRequest request, User owner) {
        return Project.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();
    }

    public void updateEntity(Project project, ProjectRequest request) {
        project.setName(request.name());
        project.setDescription(request.description());
    }
}