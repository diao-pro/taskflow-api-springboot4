package com.diao.taskflowapi.dtos.responses;

/**
 * Reponse renvoyee apres une connexion ou une inscription reussie.
 *
 * @param accessToken jeton JWT a utiliser dans l'en-tete "Authorization: Bearer ..."
 * @param tokenType   type du jeton (toujours "Bearer")
 * @param expiresIn   duree de validite du jeton, en millisecondes
 * @param user        informations publiques de l'utilisateur connecte
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserResponse user
) {
    public static AuthResponse of(String accessToken, long expiresIn, UserResponse user) {
        return new AuthResponse(accessToken, "Bearer", expiresIn, user);
    }
}