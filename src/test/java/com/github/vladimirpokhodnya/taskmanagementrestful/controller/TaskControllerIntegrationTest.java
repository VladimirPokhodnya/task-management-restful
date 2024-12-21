package com.github.vladimirpokhodnya.taskmanagementrestful.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.Task;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.TaskStatus;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskStatusDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.repository.TaskRepository;
import com.github.vladimirpokhodnya.taskmanagementrestful.service.TaskService;
import com.github.vladimirpokhodnya.taskmanagementrestful.testcontainer.PostgresContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TaskControllerIntegrationTest extends PostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        saveTestTasks();
    }

    private void saveTestTasks() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUserId(1L);
        task1.setStatus(TaskStatus.NOT_STARTED);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUserId(2L);
        task2.setStatus(TaskStatus.NOT_STARTED);

        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setDescription("Description 3");
        task3.setUserId(3L);
        task3.setStatus(TaskStatus.NOT_STARTED);

        taskRepository.saveAll(Arrays.asList(task1, task2, task3));
    }

    @Test
    @DisplayName("Тест создания задачи")
    void createTask() throws Exception {
        TaskDTO newTask = new TaskDTO();
        newTask.setTitle("New Task");
        newTask.setDescription("New Task Description");
        newTask.setUserId(1L);
        newTask.setStatus(TaskStatus.NOT_STARTED);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    @DisplayName("Тест получения задачи по ID")
    void getTaskById() throws Exception {
        Long taskId = taskRepository.findAll().get(0).getId();

        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"));
    }

    @Test
    @DisplayName("Тест обновления задачи")
    void updateTask() throws Exception {
        Long taskId = taskRepository.findAll().get(0).getId();
        TaskDTO updatedTask = new TaskDTO();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setUserId(1L);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    @DisplayName("Тест удаления задачи")
    void deleteTask() throws Exception {
        Long taskId = taskRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест удаления несуществующей задачи по ID")
    void deleteNonExistentTask() throws Exception {
        Long nonExistentTaskId = 99999L;

        mockMvc.perform(delete("/tasks/{id}", nonExistentTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест получения всех задач")
    void getAllTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Тест обновления статуса задачи")
    void updateTaskStatus() throws Exception {
        Long taskId = taskRepository.findAll().get(0).getId();
        TaskStatusDTO statusDTO = new TaskStatusDTO(taskId, TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(TaskStatus.IN_PROGRESS.toString()));
    }

    @Test
    @DisplayName("Тест обновления статуса несуществующей задачи")
    void updateNonExistentTaskStatus() throws Exception {
        Long nonExistentTaskId = 99999L;
        TaskStatusDTO statusDTO = new TaskStatusDTO(nonExistentTaskId, TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест получения несуществующей задачи по ID")
    void getNonExistentTaskById() throws Exception {
        Long nonExistentTaskId = 99999L;

        mockMvc.perform(get("/tasks/{id}", nonExistentTaskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест обновления несуществующей задачи по ID")
    void updateNonExistentTask() throws Exception {
        Long nonExistentTaskId = 99999L;
        TaskDTO updatedTask = new TaskDTO();
        updatedTask.setTitle("Updated Task");
        updatedTask.setDescription("Updated Description");
        updatedTask.setUserId(1L);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);

        mockMvc.perform(put("/tasks/{id}", nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());
    }

}
