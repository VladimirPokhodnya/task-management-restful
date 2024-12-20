package com.github.vladimirpokhodnya.taskmanagementrestful.repository;


import com.github.vladimirpokhodnya.taskmanagementrestful.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
