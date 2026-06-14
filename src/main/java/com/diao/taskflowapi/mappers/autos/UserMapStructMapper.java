package com.diao.taskflowapi.mappers.autos;

import com.diao.taskflowapi.dtos.responses.UserResponse;
import com.diao.taskflowapi.entities.User;
import org.mapstruct.Mapper;

/**
 * Version declarative (MapStruct) du mapper User -> UserResponse.
 * <p>
 * MapStruct genere automatiquement l'implementation au moment de la
 * compilation (classe {@code UserMapStructMapperImpl}), en se basant
 * sur la correspondance des noms de champs.
 *
 * @see com.diao.taskflowapi.mappers.manuals.UserManualMapper version manuelle equivalente
 */
@Mapper(componentModel = "spring")
public interface UserMapStructMapper {

    /**
     * MapStruct fait correspondre automatiquement id, fullName, email, role,
     * jobTitle et createdAt car ils portent le meme nom dans User et UserResponse.
     * Le champ "password" de User est simplement ignore (absent de UserResponse).
     */
    UserResponse toResponse(User user);
}