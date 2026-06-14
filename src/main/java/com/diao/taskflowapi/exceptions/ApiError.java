package com.diao.taskflowapi.exceptions;

import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.Map;

/**
 * Format standard de reponse d'erreur renvoye par l'API.
 *
 * @param timestamp date/heure de l'erreur
 * @param status    code HTTP
 * @param error     libelle court de l'erreur
 * @param message   message explicatif
 * @param path      chemin de la requete ayant provoque l'erreur
 * @param fieldErrors erreurs de validation par champ (null si non applicable)
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        @Nullable Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, null);
    }

    public static ApiError ofValidation(int status, String error, String message, String path,
                                          Map<String, String> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, fieldErrors);
    }
}