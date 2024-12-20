package com.github.vladimirpokhodnya.taskmanagementrestful.model.dto;


import com.github.vladimirpokhodnya.taskmanagementrestful.model.TaskStatus;

public record TaskStatusDTO(Long id, TaskStatus status) {}

