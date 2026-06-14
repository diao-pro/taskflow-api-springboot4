package com.diao.taskflowapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Un projet regroupe plusieurs taches et appartient a un proprietaire (User).
 */
@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Lors affichage de la classe (appel toString())
// EVITE : 1) Les boucles infinies (StackOverflow) si Tasks appelle Project en boucle
//        2) Les bugs de chargement (LazyInitializationException) hors transaction
@ToString(exclude = {"tasks", "owner"})
// Lors de comparaison d'instances de la classe (appel equals(Object o) et hashCode())
// INCLUT : Les champs de BaseEntity (comme l'ID) grâce à callSuper = true
// LIMITE : La comparaison au champ "name" pour éviter de charger les relations en base
@EqualsAndHashCode(callSuper = true, of = "name")
public class Project extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Nullable
    @Column(length = 1000)
    private String description;

    /**
     * Proprietaire / createur du projet.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "project", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    /**
     * Helper pour maintenir la coherence bidirectionnelle.
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }
}