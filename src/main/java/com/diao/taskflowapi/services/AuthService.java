package com.diao.taskflowapi.services;

import com.diao.taskflowapi.dtos.requests.LoginRequest;
import com.diao.taskflowapi.dtos.requests.RegisterRequest;
import com.diao.taskflowapi.dtos.responses.AuthResponse;

/**
 * Operations liees a l'authentification : inscription et connexion.
 */
public interface AuthService {

    /**
     * Cree un nouveau compte utilisateur et retourne directement
     * un token JWT (connexion automatique apres inscription).
     *
     * @throws com.diao.taskflowapi.exceptions.EmailAlreadyExistsException si l'email existe deja
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     *
     * @throws org.springframework.security.authentication.BadCredentialsException si identifiants invalides
     */
    AuthResponse login(LoginRequest request);
}