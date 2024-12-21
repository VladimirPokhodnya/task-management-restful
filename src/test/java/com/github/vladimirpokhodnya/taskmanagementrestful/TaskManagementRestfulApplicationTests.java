package com.github.vladimirpokhodnya.taskmanagementrestful;

import com.github.vladimirpokhodnya.taskmanagementrestful.testcontainer.PostgresContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskManagementRestfulApplicationTests extends PostgresContainer {

    @Test
    @DisplayName("Контекст успешно инициализируется")
    void contextLoads() {
    }

}
