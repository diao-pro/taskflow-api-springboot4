package com.diao.taskflowapi;

import com.diao.taskflowapi.dtos.requests.LoginRequest;
import com.diao.taskflowapi.dtos.requests.ProjectRequest;
import com.diao.taskflowapi.dtos.requests.RegisterRequest;
import com.diao.taskflowapi.dtos.requests.TaskRequest;
import com.diao.taskflowapi.dtos.requests.TaskStatusUpdateRequest;
import com.diao.taskflowapi.enums.TaskPriority;
import com.diao.taskflowapi.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
// Import pour get(), post(), patch(), put(), etc.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// Import pour status(), content(), etc.
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test d'integration "bout en bout" du flux principal :
 * inscription -> connexion -> creation de projet -> creation de tache
 * -> mise a jour du statut -> recuperation du detail.
 * <p>
 * Verifie egalement que les endpoints sont bien proteges par JWT,
 * et que le versioning d'API (v1/v2) repond correctement.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskflowApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullWorkflow_register_login_createProject_createTask_updateStatus() throws Exception {

        // 1. Inscription
        RegisterRequest register = new RegisterRequest("Test User", "test.user@taskflow.dev", "Password123");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.email").value("test.user@taskflow.dev"));

        // 2. Connexion
        LoginRequest login = new LoginRequest("test.user@taskflow.dev", "Password123");
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("accessToken").asText();
        String authHeader = "Bearer " + token;

        // 3. Acces refuse sans token
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isUnauthorized());

        // 4. Creation d'un projet
        ProjectRequest projectRequest = new ProjectRequest("Projet de test", "Description du projet de test");
        String projectResponse = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Projet de test"))
                .andExpect(jsonPath("$.totalTasks").value(0))
                .andReturn().getResponse().getContentAsString();

        Long projectId = objectMapper.readTree(projectResponse).get("id").asLong();

        // 5. Creation d'une tache dans ce projet
        TaskRequest taskRequest = new TaskRequest(
                "Ma premiere tache",
                "Description de la tache",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                null,
                projectId,
                null
        );
        String taskResponse = mockMvc.perform(post("/api/v1/tasks")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Ma premiere tache"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(taskResponse).get("id").asLong();

        // 6. Mise a jour du statut (PATCH)
        TaskStatusUpdateRequest statusUpdate = new TaskStatusUpdateRequest(TaskStatus.IN_PROGRESS);
        mockMvc.perform(patch("/api/v1/tasks/{id}/status", taskId)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        // 7. Detail du projet : doit maintenant contenir 1 tache
        mockMvc.perform(get("/api/v1/projects/{id}", projectId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTasks").value(1))
                .andExpect(jsonPath("$.tasks[0].id").value(taskId));

        // 8. Detail de la tache en v1 (par defaut)
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));

        // 9. Detail de la tache en v2 (via header de versioning)
        mockMvc.perform(get("/api/v1/tasks/{id}", taskId)
                        .header("Authorization", authHeader)
                        .header("X-API-Version", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        // Compte cree par le DataSeeder
        LoginRequest login = new LoginRequest("alice@taskflow.dev", "WrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_withExistingEmail_returnsConflict() throws Exception {
        RegisterRequest register = new RegisterRequest("Alice Clone", "alice@taskflow.dev", "Password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isConflict());
    }

    @Test
    void contextLoads() {
    }

}
