package com.diao.taskflowapi.controllers;

import com.diao.taskflowapi.dtos.requests.LoginRequest;
import com.diao.taskflowapi.dtos.requests.RegisterRequest;
import com.diao.taskflowapi.dtos.responses.AuthResponse;
import com.diao.taskflowapi.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints publics d'authentification : inscription et connexion.
 * <p>
 * Ne necessitent pas de token JWT (voir {@link com.diao.taskflowapi.configs.SecurityConfig}).
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion des utilisateurs")
@SecurityRequirements // desactive l'exigence de bearer token pour ce controller dans Swagger
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Creer un nouveau compte utilisateur",
            description = "Cree un compte avec le role ROLE_USER et retourne directement un token JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}