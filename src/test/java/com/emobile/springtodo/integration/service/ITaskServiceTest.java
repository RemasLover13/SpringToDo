package com.emobile.springtodo.integration.service;

import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.integration.config.TestContainerConfig;
import com.emobile.springtodo.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@DisplayName("Integration tests for TaskService")
class ITaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    @DisplayName("Should create a new task and return DTO")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldCreateTaskAndReturnDTO() {
        CreateTaskDTO createTaskDTO = CreateTaskDTO.builder()
                .title("New Task")
                .description("New Description")
                .status(Status.PENDING)
                .build();

        TaskDTO createdTask = taskService.createTask(createTaskDTO);

        Assertions.assertNotNull(createdTask.getId());
        Assertions.assertEquals("New Task", createdTask.getTitle());
        Assertions.assertEquals("New Description", createdTask.getDescription());
        Assertions.assertEquals(Status.PENDING, createdTask.getStatus());
    }

    @Test
    @DisplayName("Should update an existing task and return DTO")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldUpdateTaskAndReturnDTO() {
        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .build();

        TaskDTO updatedTask = taskService.updateTask(updateTaskDto, 1L);

        Assertions.assertNotNull(updatedTask);
        Assertions.assertEquals("Updated Title", updatedTask.getTitle());
        Assertions.assertEquals("Updated Description", updatedTask.getDescription());
        Assertions.assertEquals(Status.COMPLETED, updatedTask.getStatus());
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found during update")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundDuringUpdate() {
        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .build();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(updateTaskDto, 999L);
        });

        Assertions.assertEquals("Task with id 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should find task by ID and return DTO")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldFindTaskByIdAndReturnDTO() {
        TaskDTO task = taskService.getTaskById(1L);

        Assertions.assertNotNull(task);
        Assertions.assertEquals("Task 1", task.getTitle());
        Assertions.assertEquals("Description 1", task.getDescription());
        Assertions.assertEquals(Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found by ID")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundById() {
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(999L);
        });

        Assertions.assertEquals("Task with id 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should find paginated tasks and return DTOs")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldFindPaginatedTasksAndReturnDTOs() {
        List<TaskDTO> tasks = taskService.getTasks(0, 10);

        Assertions.assertNotNull(tasks);
        Assertions.assertFalse(tasks.isEmpty());
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals("Task 1", tasks.get(0).getTitle());
        Assertions.assertEquals("Task 2", tasks.get(1).getTitle());
    }

    @Test
    @DisplayName("Should delete task by ID")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldDeleteTaskById() {
        taskService.deleteTask(1L);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });

        Assertions.assertEquals("Task with id 1 not found", exception.getMessage());
    }
}