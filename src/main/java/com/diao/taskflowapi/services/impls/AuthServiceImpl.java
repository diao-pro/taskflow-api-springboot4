package com.diao.taskflowapi.services.impls;

import com.diao.taskflowapi.dtos.requests.LoginRequest;
import com.diao.taskflowapi.dtos.requests.RegisterRequest;
import com.diao.taskflowapi.dtos.responses.AuthResponse;
import com.diao.taskflowapi.dtos.responses.UserResponse;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.enums.Role;
import com.diao.taskflowapi.exceptions.EmailAlreadyExistsException;
import com.diao.taskflowapi.mappers.autos.UserMapStructMapper;
import com.diao.taskflowapi.repositories.UserRepository;
import com.diao.taskflowapi.securities.CustomUserDetails;
import com.diao.taskflowapi.securities.JwtService;
import com.diao.taskflowapi.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation des operations d'authentification.
 * <p>
 * Utilise le mapper MapStruct pour la conversion User -> UserResponse
 * (le mapper manuel est utilise du cote des projets/taches a titre de comparaison).
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapStructMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(new CustomUserDetails(saved));
        UserResponse userResponse = userMapper.toResponse(saved);

        return AuthResponse.of(token, jwtService.getExpirationMs(), userResponse);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Delegue la verification email/mot de passe a Spring Security.
        // Leve BadCredentialsException si invalide (gere par GlobalExceptionHandler).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable apres authentification reussie"));

        String token = jwtService.generateToken(new CustomUserDetails(user));
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(token, jwtService.getExpirationMs(), userResponse);
    }
}