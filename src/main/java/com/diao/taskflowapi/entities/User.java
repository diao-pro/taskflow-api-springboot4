package com.diao.taskflowapi.entities;

import com.diao.taskflowapi.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Utilisateur de l'application (auteur de taches, membre de projets...).
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Lors affichage de la classe (appel toString())
// EVITE : Affichage du mot de passe
@ToString(exclude = "password")
// Lors de comparaison d'instances de la classe (appel equals(Object o) et hashCode())
// INCLUT : Les champs de BaseEntity (comme l'ID) grâce à callSuper = true
// LIMITE : La comparaison au champ "email" pour éviter de charger les relations en base
@EqualsAndHashCode(callSuper = true, of = "email")
public class User extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Mot de passe encode (BCrypt). Jamais expose dans les DTOs de sortie.
     */
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    /**
     * Poste / fonction de l'utilisateur. Champ optionnel -> nullable explicite via JSpecify.
     */
    @Nullable
    @Column(length = 100)
    private String jobTitle;

    @Builder.Default
    private boolean enabled = true;
}