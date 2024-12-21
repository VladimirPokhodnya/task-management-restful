package com.github.vladimirpokhodnya.taskmanagementrestful.controller;

import com.github.vladimirpokhodnya.taskmanagementrestful.exception.TaskNotFoundException;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.model.dto.TaskStatusDTO;
import com.github.vladimirpokhodnya.taskmanagementrestful.service.TaskService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        if (!taskService.deleteTask(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PatchMapping
    public TaskDTO updateTaskStatus(@RequestBody TaskStatusDTO statusDTO) {
        return taskService.updateStatus(statusDTO.id(), statusDTO.status())
                .orElseThrow(() -> new TaskNotFoundException(statusDTO.id()));
    }
}
