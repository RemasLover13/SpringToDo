package com.emobile.springtodo.unit.dao;

import com.emobile.springtodo.dao.TaskDaoImpl;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.exception.TaskNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TaskDaoImpl")
class TaskDaoTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TaskDaoImpl taskDao;

    private static final Long TASK_ID = 1L;
    private static final String TASK_TITLE = "Implement interface";
    private static final String TASK_DESCRIPTION = "Implement interface for testing";
    private static final Status TASK_STATUS = Status.PENDING;
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

    private final Task TASK = Task.builder()
            .id(TASK_ID)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .status(TASK_STATUS)
            .createdAt(CREATED_AT)
            .updatedAt(UPDATED_AT)
            .build();

    @Test
    @DisplayName("Should return paginated tasks")
    void shouldReturnPaginatedTasks() {
        int offset = 0;
        int limit = 10;
        List<Task> expectedTasks = List.of(TASK);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(limit), eq(offset)))
                .thenReturn(expectedTasks);

        List<Task> actualTasks = taskDao.findTasksWithPagination(offset, limit);

        assertNotNull(actualTasks);
        assertEquals(expectedTasks.size(), actualTasks.size());
        assertEquals(expectedTasks.get(0).getId(), actualTasks.get(0).getId());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(limit), eq(offset));
    }

    @Test
    @DisplayName("Should return empty list if no tasks found")
    void shouldReturnEmptyListIfNoTasksFound() {
        int offset = 0;
        int limit = 10;
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(limit), eq(offset)))
                .thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskDao.findTasksWithPagination(offset, limit);

        assertTrue(actualTasks.isEmpty());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class), eq(limit), eq(offset));
    }

    @Test
    @DisplayName("Should return task by ID")
    void shouldReturnTaskById() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(TASK_ID)))
                .thenReturn(TASK);

        Task actualTask = taskDao.findById(TASK_ID);

        assertNotNull(actualTask);
        assertEquals(TASK.getId(), actualTask.getId());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found")
    void shouldThrowTaskNotFoundExceptionIfTaskNotFound() {
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), eq(TASK_ID)))
                .thenThrow(new EmptyResultDataAccessException(1));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskDao.findById(TASK_ID));
        assertEquals("Task with id " + TASK_ID + " not found", exception.getMessage());
        verify(jdbcTemplate).queryForObject(anyString(), any(RowMapper.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should delete task by ID")
    void shouldDeleteTaskById() {
        doReturn(1).when(jdbcTemplate).update(anyString(), eq(TASK_ID));

        taskDao.deleteById(TASK_ID);

        verify(jdbcTemplate).update(anyString(), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should throw RuntimeException if failed to delete task")
    void shouldThrowRuntimeExceptionIfFailedToDeleteTask() {
        doReturn(0).when(jdbcTemplate).update(anyString(), eq(TASK_ID));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskDao.deleteById(TASK_ID));
        assertEquals("Failed to delete task by id " + TASK_ID, exception.getMessage());
        verify(jdbcTemplate).update(anyString(), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should save new task and generate ID")
    void shouldSaveNewTaskAndGenerateId() {
        doAnswer(invocation -> {
            PreparedStatementCreator psc = invocation.getArgument(0);
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1L));
            return 1;
        }).when(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));

        Task savedTask = taskDao.save(TASK);

        assertNotNull(savedTask.getId());
        assertEquals(1L, savedTask.getId());
        assertEquals("Implement interface", savedTask.getTitle());
        verify(jdbcTemplate).update(any(PreparedStatementCreator.class), any(KeyHolder.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if task data is invalid")
    void shouldThrowIllegalArgumentExceptionIfTaskDataIsInvalid() {
        Task invalidTask = new Task();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskDao.save(invalidTask));
        assertEquals("Task data is invalid", exception.getMessage());
    }

    @Test
    @DisplayName("Should update existing task")
    void shouldUpdateExistingTask() {
        Task updatedTask = TASK.toBuilder().title("Updated Title").build();
        doReturn(1).when(jdbcTemplate).update(anyString(), any(Object[].class));

        Task result = taskDao.update(updatedTask, TASK_ID);

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        verify(jdbcTemplate).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found during update")
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundDuringUpdate() {
        Task updatedTask = TASK.toBuilder().title("Updated Title").build();
        doReturn(0).when(jdbcTemplate).update(anyString(), any(Object[].class));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskDao.update(updatedTask, TASK_ID));
        assertEquals("Task with id " + TASK_ID + " not found", exception.getMessage());
        verify(jdbcTemplate).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if no fields to update")
    void shouldThrowIllegalArgumentExceptionIfNoFieldsToUpdate() {
        Task updatedTask = new Task();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskDao.update(updatedTask, TASK_ID));
        assertEquals("No fields to update", exception.getMessage());
    }
}