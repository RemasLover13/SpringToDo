package com.emobile.springtodo.integration.dao;

import com.emobile.springtodo.dao.TaskDao;
import com.emobile.springtodo.dao.TaskDaoImpl;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.integration.config.TestContainerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@DisplayName("Integration tests for TaskDao")
class ITaskDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private TaskDao taskDao;

    @BeforeEach
    void setUp() {
        taskDao = new TaskDaoImpl(jdbcTemplate);
    }

    @Test
    @Transactional
    @DisplayName("Should save a new task and generate ID")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldSaveNewTaskAndGenerateId() {
        Task task = Task.builder()
                .title("New Task")
                .description("New Description")
                .status(Status.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Task savedTask = taskDao.save(task);

        Assertions.assertNotNull(savedTask.getId());
        Assertions.assertEquals("New Task", savedTask.getTitle());
    }

    @Test
    @Transactional
    @DisplayName("Should find task by ID")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldFindTaskById() {
        Task task = taskDao.findById(1L);

        Assertions.assertNotNull(task);
        Assertions.assertEquals("Task 1", task.getTitle());
    }

    @Test
    @Transactional
    @DisplayName("Should throw TaskNotFoundException if task not found by ID")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundById() {
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskDao.findById(999L));
        Assertions.assertEquals("Task with id 999 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete task by ID")
    @Transactional
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldDeleteTaskById() {
        taskDao.deleteById(1L);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskDao.findById(1L));
        Assertions.assertEquals("Task with id 1 not found", exception.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Should update an existing task")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldUpdateExistingTask() {
        Task updatedTask = Task.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Task result = taskDao.update(updatedTask, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Title", result.getTitle());
    }

    @Test
    @Transactional
    @DisplayName("Should throw TaskNotFoundException if task not found during update")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundDuringUpdate() {
        Task updatedTask = Task.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskDao.update(updatedTask, 999L));
        Assertions.assertEquals("Task with id 999 not found", exception.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("Should find paginated tasks")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldFindPaginatedTasks() {
        List<Task> tasks = taskDao.findTasksWithPagination(0, 10);

        Assertions.assertNotNull(tasks);
        Assertions.assertFalse(tasks.isEmpty());
        Assertions.assertEquals(2, tasks.size());
        Assertions.assertEquals("Task 1", tasks.get(0).getTitle());
    }
}