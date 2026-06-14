package com.diao.taskflowapi.securities;

import com.diao.taskflowapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Charge les informations d'authentification d'un utilisateur a partir de son email.
 * <p>
 * Retourne directement l'entite {@link com.taskflow.entity.User}, qui implemente
 * {@link UserDetails} (voir {@link CustomUserDetails}).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur trouve avec l'email : " + email));
    }
}