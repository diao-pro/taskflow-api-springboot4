package com.diao.taskflowapi.exceptions;

/**
 * Levee lors d'une tentative d'inscription avec un email deja existant.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Un compte existe deja avec l'adresse email : " + email);
    }
}