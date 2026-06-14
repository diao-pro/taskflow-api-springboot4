package com.diao.taskflowapi.mappers.autos;

import com.diao.taskflowapi.dtos.responses.ProjectResponse;
import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.dtos.responses.UserResponse;
import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Version "declarative" du mapper Project, basee sur MapStruct pour les
 * sous-objets ({@link UserMapStructMapper}, {@link TaskMapStructMapper}).
 * <p>
 * {@link ProjectResponse} etant un record immuable contenant des champs
 * calcules (totalTasks, completedTasks) qui n'existent pas tels quels sur
 * l'entite {@link Project}, MapStruct seul ne peut pas generer ce mapping
 * de bout en bout de maniere idiomatique. On delegue donc :
 * <ul>
 *     <li>le mapping de {@code User -> UserResponse} a MapStruct ({@link UserMapStructMapper})</li>
 *     <li>le mapping de {@code Task -> TaskResponse} a MapStruct ({@link TaskMapStructMapper})</li>
 *     <li>l'assemblage final du record et le calcul des compteurs a cette classe</li>
 * </ul>
 *
 * @see com.diao.taskflowapi.mappers.manuals.ProjectManualMapper version 100% manuelle equivalente
 */
@Component("projectMapStructMapper")
@RequiredArgsConstructor
public class ProjectMapStructMapper {

    private final UserMapStructMapper userMapper;
    private final TaskMapStructMapper taskMapper;

    /**
     * Vue resumee : pas de detail des taches, uniquement les compteurs.
     */
    public ProjectResponse toSummaryResponse(Project project) {
        if (project == null) {
            return null;
        }

        UserResponse owner = userMapper.toResponse(project.getOwner());

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                owner,
                project.getTasks().size(),
                countDone(project),
                project.getCreatedAt(),
                null
        );
    }

    /**
     * Vue detaillee : inclut la liste complete des taches mappees via MapStruct.
     */
    public ProjectResponse toDetailedResponse(Project project) {
        if (project == null) {
            return null;
        }

        UserResponse owner = userMapper.toResponse(project.getOwner());

        List<TaskResponse> tasks = project.getTasks().stream()
                .map(taskMapper::toResponse)
                .toList();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                owner,
                project.getTasks().size(),
                countDone(project),
                project.getCreatedAt(),
                tasks
        );
    }

    private int countDone(Project project) {
        return (int) project.getTasks().stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();
    }
}