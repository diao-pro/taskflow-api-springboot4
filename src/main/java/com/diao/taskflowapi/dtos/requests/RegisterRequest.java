package com.diao.taskflowapi.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Donnees necessaires pour creer un nouveau compte utilisateur.
 *
 * @param fullName nom complet de l'utilisateur
 * @param email    adresse email (unique)
 * @param password mot de passe en clair (sera encode)
 */
public record RegisterRequest(

        @NotBlank(message = "Le nom complet est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caracteres")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caracteres")
        String password
) {
}