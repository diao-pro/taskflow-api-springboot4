package com.diao.taskflowapi.mappers.autos;

import com.diao.taskflowapi.dtos.responses.TaskResponse;
import com.diao.taskflowapi.entities.Task;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

/**
 * Version declarative (MapStruct) du mapper Task -> TaskResponse.
 * <p>
 * Demonstration des annotations {@code @Mapping} pour gerer les champs
 * dont le nom ou le chemin differe entre l'entite et le DTO
 * (ex : project.id -> projectId).
 *
 * @see com.diao.taskflowapi.mappers.manuals.TaskManualMapper version manuelle equivalente
 */
@Mapper(componentModel = "spring", uses = UserMapStructMapper.class)
public interface TaskMapStructMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "assignee", target = "assignee") // delegue a UserMapStructMapper (peut etre null)
    TaskResponse toResponse(Task task);
}