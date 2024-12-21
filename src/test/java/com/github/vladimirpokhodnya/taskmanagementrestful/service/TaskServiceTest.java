package com.github.vladimirpokhodnya.taskmanagementrestful.service;

import com.github.vladimirpokhodnya.taskmanagementrestful.model.Task;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.TaskStatus;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository);
    }

    @Test
    @DisplayName("Тест создания задачи")
    void createTask_shouldReturnTaskDTO_whenTaskIsCreated() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Test Task");
        taskDTO.setDescription("Test Description");
        taskDTO.setUserId(1L);
        taskDTO.setStatus(TaskStatus.NOT_STARTED);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.NOT_STARTED);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO createdTaskDTO = taskService.createTask(taskDTO);

        assertNotNull(createdTaskDTO);
        assertEquals(taskDTO.getTitle(), createdTaskDTO.getTitle());
        assertEquals(taskDTO.getDescription(), createdTaskDTO.getDescription());
    }

    @Test
    @DisplayName("Тест поиска задачи по id")
    void getTaskById_shouldReturnTaskDTO_whenTaskExists() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setUserId(1L);
        task.setStatus(TaskStatus.NOT_STARTED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Optional<TaskDTO> taskDTO = taskService.getTaskById(taskId);

        assertTrue(taskDTO.isPresent());
        assertEquals(taskId, taskDTO.get().getId());
    }

    @Test
    @DisplayName("Тест поиска задачи по id, когда такой задачи нет")
    void getTaskById_shouldReturnEmpty_whenTaskDoesNotExist() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<TaskDTO> taskDTO = taskService.getTaskById(taskId);

        assertFalse(taskDTO.isPresent());
    }

    @Test
    @DisplayName("Тест обновления задачи")
    void updateTask_shouldReturnUpdatedTaskDTO_whenTaskExists() {
        Long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setTitle("Old Task");
        existingTask.setDescription("Old Description");
        existingTask.setUserId(1L);
        existingTask.setStatus(TaskStatus.COMPLETED);

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setUserId(1L);
        taskDTO.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        Optional<TaskDTO> updatedTaskDTO = taskService.updateTask(taskId, taskDTO);

        assertTrue(updatedTaskDTO.isPresent());
        assertEquals("Updated Task", updatedTaskDTO.get().getTitle());
        assertEquals("Updated Description", updatedTaskDTO.get().getDescription());
    }

    @Test
    @DisplayName("Тест обновления задачи, когда такой задачи нет")
    void updateTask_shouldReturnEmpty_whenTaskDoesNotExist() {
        Long taskId = 1L;
        TaskDTO taskDTO = new TaskDTO();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<TaskDTO> updatedTaskDTO = taskService.updateTask(taskId, taskDTO);
        assertFalse(updatedTaskDTO.isPresent());
    }

    @Test
    @DisplayName("Тест удаления задачи")
    void deleteTask_shouldCallRepositoryDelete_whenTaskExists() {
        Long taskId = 1L;

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    @DisplayName("Тест поиска всех задач")
    void getAllTasks_shouldReturnListOfTaskDTOs() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setUserId(1L);
        task.setStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskDTO> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
    }

    @Test
    @DisplayName("Тест обновления статуса задачи")
    void updateStatus_shouldReturnUpdatedTaskDTO_whenTaskExists() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Optional<TaskDTO> updatedTaskDTO = taskService.updateStatus(taskId, TaskStatus.IN_PROGRESS);

        assertTrue(updatedTaskDTO.isPresent());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTaskDTO.get().getStatus());
    }

    @Test
    @DisplayName("Тест обновления статуса, когда задача не найдена ")
    void updateStatus_shouldReturnEmpty_whenTaskDoesNotExist() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        Optional<TaskDTO> updatedTaskDTO = taskService.updateStatus(taskId, TaskStatus.IN_PROGRESS);

        assertFalse(updatedTaskDTO.isPresent());
    }
}
