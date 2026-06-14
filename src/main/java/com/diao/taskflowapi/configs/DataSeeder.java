package com.diao.taskflowapi.configs;

import com.diao.taskflowapi.entities.Project;
import com.diao.taskflowapi.entities.Task;
import com.diao.taskflowapi.entities.User;
import com.diao.taskflowapi.enums.Role;
import com.diao.taskflowapi.enums.TaskPriority;
import com.diao.taskflowapi.enums.TaskStatus;
import com.diao.taskflowapi.repositories.ProjectRepository;
import com.diao.taskflowapi.repositories.TaskRepository;
import com.diao.taskflowapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initialise quelques donnees de demonstration au demarrage de l'application :
 * un administrateur, un utilisateur standard, un projet et quelques taches.
 * <p>
 * Comptes crees :
 * <ul>
 *     <li>admin@diao.taskflowapi.dev / Admin123! (ROLE_ADMIN)</li>
 *     <li>ibrahim@diao.taskflowapi.dev / Ibrahim123! (ROLE_USER)</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // deja initialise
        }

        User admin = userRepository.save(User.builder()
                .fullName("Admin TaskFlow")
                .email("admin@taskflow.dev")
                .password(passwordEncoder.encode("Admin123!"))
                .role(Role.ROLE_ADMIN)
                .jobTitle("Administrateur systeme")
                .enabled(true)
                .build());

        User ibrahim = userRepository.save(User.builder()
                .fullName("Ibrahim Diao")
                .email("ibrahim@taskflow.dev")
                .password(passwordEncoder.encode("Ibrahim123!"))
                .role(Role.ROLE_USER)
                .jobTitle("Developpeur Full Stack")
                .enabled(true)
                .build());

        Project project = projectRepository.save(Project.builder()
                .name("Refonte API TaskFlow")
                .description("Migration de l'API vers Spring Boot 4 / Spring Framework 7")
                .owner(ibrahim)
                .build());

        taskRepository.save(Task.builder()
                .title("Mettre en place l'authentification JWT")
                .description("Login, register, filtre JWT, Spring Security 7")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDate.now().minusDays(2))
                .project(project)
                .assignee(ibrahim)
                .build());

        taskRepository.save(Task.builder()
                .title("Migrer vers Jackson 3")
                .description("Adapter les serializers/deserializers custom")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.CRITICAL)
                .dueDate(LocalDate.now().plusDays(3))
                .project(project)
                .assignee(ibrahim)
                .build());

        taskRepository.save(Task.builder()
                .title("Documenter l'API avec Swagger")
                .description("Ajouter les annotations OpenAPI sur tous les controllers")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .dueDate(LocalDate.now().plusDays(7))
                .project(project)
                .assignee(null)
                .build());

        taskRepository.save(Task.builder()
                .title("Activer les virtual threads")
                .description("spring.threads.virtual.enabled=true + tests de charge")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .dueDate(null)
                .project(project)
                .assignee(admin)
                .build());
    }
}