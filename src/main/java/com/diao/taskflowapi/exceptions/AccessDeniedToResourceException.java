package com.diao.taskflowapi.exceptions;

/**
 * Levee lorsqu'un utilisateur tente d'agir sur une ressource
 * qui ne lui appartient pas (et qu'il n'est pas administrateur).
 */
public class AccessDeniedToResourceException extends RuntimeException {

    public AccessDeniedToResourceException(String message) {
        super(message);
    }
}