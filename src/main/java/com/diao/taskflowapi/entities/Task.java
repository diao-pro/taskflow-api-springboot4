package com.diao.taskflowapi.entities;

import com.diao.taskflowapi.enums.TaskPriority;
import com.diao.taskflowapi.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

/**
 * Une tache appartient a un projet et peut etre assignee a un utilisateur.
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Lors affichage de la classe (appel toString())
// EVITE : 1) Les boucles infinies (StackOverflow) si Project appelle Task en boucle
//        2) Les bugs de chargement (LazyInitializationException) hors transaction
@ToString(exclude = {"project", "assignee"})
// Lors de comparaison d'instances de la classe (appel equals(Object o) et hashCode())
// INCLUT : Les champs de BaseEntity (comme l'ID) grâce à callSuper = true
// LIMITE : La comparaison au champ "title" pour éviter de charger les relations en base
@EqualsAndHashCode(callSuper = true, of = "title")
public class Task extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Nullable
    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    /**
     * Date d'echeance. Optionnelle.
     */
    @Nullable
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Utilisateur assigne. Peut etre absent (tache non assignee).
     */
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
}