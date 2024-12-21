package com.github.vladimirpokhodnya.taskmanagementrestful.service;

import com.github.vladimirpokhodnya.taskmanagementrestful.model.Task;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.TaskStatus;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.repository.TaskRepository;
import com.github.vladimirpokhodnya.taskmanagementrestful.testcontainer.PostgresContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TaskServiceIntegrationTest extends PostgresContainer {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("Тест создания задачи и возврата TaskDTO")
    void createTask_shouldCreateAndReturnTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setUserId(1L);
        taskDTO.setStatus(TaskStatus.NOT_STARTED);

        TaskDTO createdTaskDTO = taskService.createTask(taskDTO);

        assertNotNull(createdTaskDTO.getId());
        assertEquals(taskDTO.getTitle(), createdTaskDTO.getTitle());
        assertEquals(taskDTO.getDescription(), createdTaskDTO.getDescription());
        assertEquals(taskDTO.getUserId(), createdTaskDTO.getUserId());
        assertEquals(taskDTO.getStatus(), createdTaskDTO.getStatus());
    }

    @Test
    @DisplayName("Тест получения задачи по ID, когда задача существует")
    void getTaskById_shouldReturnTaskDTO_whenTaskExists() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.NOT_STARTED);
        taskRepository.save(task);

        Optional<TaskDTO> foundTaskDTO = taskService.getTaskById(task.getId());

        assertTrue(foundTaskDTO.isPresent());
        assertEquals("Test Task", foundTaskDTO.get().getTitle());
    }

    @Test
    @DisplayName("Тест обновления задачи и возврата обновленного TaskDTO")
    void updateTask_shouldUpdateAndReturnUpdatedTaskDTO() {
        Task task = new Task();
        task.setTitle("Old Task");
        task.setDescription("Old Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.NOT_STARTED);
        taskRepository.save(task);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setUserId(1L);
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);

        Optional<TaskDTO> updatedTaskDTO = taskService.updateTask(task.getId(), taskDTO);

        assertTrue(updatedTaskDTO.isPresent());
        assertEquals("Updated Task", updatedTaskDTO.get().getTitle());
        assertEquals("Updated Description", updatedTaskDTO.get().getDescription());
    }

    @Test
    @DisplayName("Тест удаления задачи по ID")
    void deleteTask_shouldDeleteTaskById() {
        Task task = new Task();
        task.setTitle("Task to delete");
        task.setDescription("This task will be deleted");
        task.setUserId(1L);
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        taskService.deleteTask(task.getId());

        Optional<TaskDTO> foundTaskDTO = taskService.getTaskById(task.getId());
        assertFalse(foundTaskDTO.isPresent());
    }

    @Test
    @DisplayName("Тест получения всех задач и возврата списка TaskDTO")
    void getAllTasks_shouldReturnListOfTaskDTOs() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setUserId(1L);
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setUserId(2L);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task2);

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Тест обновления статуса задачи")
    void updateStatus_shouldUpdateAndReturnUpdatedTaskDTO() {
        Task task = new Task();
        task.setTitle("Task to update status");
        task.setDescription("Updating status");
        task.setUserId(1L);
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);

        Optional<TaskDTO> updatedTaskDTO = taskService.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);

        assertTrue(updatedTaskDTO.isPresent());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskDTO.get().getStatus());
    }

    @Test
    @DisplayName("Тест обновления задачи, когда задача не существует")
    void updateTask_shouldReturnEmpty_whenTaskDoesNotExist() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Non-existent Task");
        taskDTO.setDescription("This task does not exist");
        taskDTO.setUserId(1L);
        taskDTO.setStatus(TaskStatus.NOT_STARTED);

        Optional<TaskDTO> updatedTaskDTO = taskService.updateTask(9999L, taskDTO);

        assertFalse(updatedTaskDTO.isPresent());
    }

    @Test
    @DisplayName("Тест удаления задачи, когда задача не существует")
    void deleteTask_shouldNotThrowException_whenTaskDoesNotExist() {
        assertDoesNotThrow(() -> taskService.deleteTask(9999L));
    }

    @Test
    @DisplayName("Тест обновления статуса, когда задача не существует")
    void updateStatus_shouldReturnEmpty_whenTaskDoesNotExist() {
        Optional<TaskDTO> updatedTaskDTO = taskService.updateStatus(9999L, TaskStatus.IN_PROGRESS);

        assertFalse(updatedTaskDTO.isPresent());
    }
}
