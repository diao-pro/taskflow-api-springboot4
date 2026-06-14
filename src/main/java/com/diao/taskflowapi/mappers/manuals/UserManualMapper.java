package com.diao.taskflowapi.mappers.manuals;

import com.diao.taskflowapi.dtos.responses.UserResponse;
import com.diao.taskflowapi.entities.User;
import org.springframework.stereotype.Component;

/**
 * Version "classique" du mapper User -> UserResponse.
 * <p>
 * Conversion manuelle, explicite, sans generation de code.
 * Utile pour comprendre exactement ce qui se passe, ou pour des
 * transformations complexes/conditionnelles que MapStruct gererait moins bien.
 *
 * @see com.taskflow.mapper.mapstruct.UserMapStructMapper version declarative equivalente
 */
@Component("userManualMapper")
public class UserManualMapper {

    /**
     * Convertit une entite User en DTO de reponse, en excluant
     * volontairement le mot de passe.
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getJobTitle(),
                user.getCreatedAt()
        );
    }
}