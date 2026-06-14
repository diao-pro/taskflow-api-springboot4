package com.diao.taskflowapi.exceptions;

/**
 * Levee lorsqu'une ressource (User, Project, Task...) n'est pas trouvee en base.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object identifier) {
        return new ResourceNotFoundException(resource + " introuvable avec l'identifiant : " + identifier);
    }
}