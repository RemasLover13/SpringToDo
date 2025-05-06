package com.emobile.springtodo.unit.service;

import com.emobile.springtodo.dao.TaskDao;
import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.mapper.CreateTaskDtoMapper;
import com.emobile.springtodo.mapper.TaskDtoMapper;
import com.emobile.springtodo.mapper.UpdateTaskDtoMapper;
import com.emobile.springtodo.service.TaskServiceImpl;
import com.emobile.springtodo.util.TaskValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for TaskServiceImpl")
class TaskServiceTest {

    @Mock
    private TaskDao taskDao;

    @Mock
    private CreateTaskDtoMapper createTaskDtoMapper;

    @Mock
    private UpdateTaskDtoMapper updateTaskDtoMapper;

    @Mock
    private TaskDtoMapper taskDtoMapper;

    @Mock
    private TaskValidator taskValidator;

    @InjectMocks
    private TaskServiceImpl taskService;

    private static final Long TASK_ID = 1L;
    private static final String TASK_TITLE = "Add documentation";
    private static final String TASK_DESCRIPTION = "Add complete documentation";
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

    private final CreateTaskDTO CREATE_TASK_DTO = CreateTaskDTO.builder()
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .status(TASK_STATUS)
            .build();

    private final UpdateTaskDto UPDATE_TASK_DTO = UpdateTaskDto.builder()
            .title("Updated Title")
            .description("Updated Description")
            .status(Status.COMPLETED)
            .build();

    private final TaskDTO TASK_DTO = TaskDTO.builder()
            .id(TASK_ID)
            .title(TASK_TITLE)
            .description(TASK_DESCRIPTION)
            .status(TASK_STATUS)
            .createdAt(CREATED_AT)
            .updatedAt(UPDATED_AT)
            .build();

    @Test
    @DisplayName("Should create a new task and return TaskDTO")
    void shouldCreateNewTaskAndReturnTaskDTO() {
        when(createTaskDtoMapper.mapToEntity(CREATE_TASK_DTO)).thenReturn(TASK);
        when(taskDao.save(any(Task.class))).thenReturn(TASK);
        when(taskDtoMapper.mapToDTO(TASK)).thenReturn(TASK_DTO);

        TaskDTO result = taskService.createTask(CREATE_TASK_DTO);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        assertEquals(TASK_TITLE, result.getTitle());
        verify(taskDao).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if createTaskDTO is invalid")
    void shouldThrowIllegalArgumentExceptionIfCreateTaskDTOIsInvalid() {
        CreateTaskDTO invalidDto = new CreateTaskDTO();
        when(createTaskDtoMapper.mapToEntity(invalidDto)).thenThrow(new IllegalArgumentException("Invalid task data"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(invalidDto));
        assertEquals("Invalid task data", exception.getMessage());
        verify(taskDao, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should update an existing task and return TaskDTO")
    void shouldUpdateExistingTaskAndReturnTaskDTO() {
        Task updatedTask = TASK.toBuilder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskDao.findById(TASK_ID)).thenReturn(TASK);
        when(updateTaskDtoMapper.mapToEntity(UPDATE_TASK_DTO)).thenReturn(updatedTask);
        when(taskDao.update(updatedTask, TASK_ID)).thenReturn(updatedTask);
        when(taskDtoMapper.mapToDTO(updatedTask)).thenReturn(TaskDTO.builder()
                .id(TASK_ID)
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .createdAt(CREATED_AT)
                .updatedAt(LocalDateTime.now())
                .build());

        TaskDTO result = taskService.updateTask(UPDATE_TASK_DTO, TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(taskDao).update(updatedTask, TASK_ID);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found during update")
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundDuringUpdate() {
        when(taskDao.findById(TASK_ID)).thenThrow(new TaskNotFoundException("Task with id " + TASK_ID + " not found"));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(UPDATE_TASK_DTO, TASK_ID));
        assertEquals("Task with id " + TASK_ID + " not found", exception.getMessage());
        verify(taskDao, never()).update(any(Task.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if updateTaskDTO is invalid")
    void shouldThrowIllegalArgumentExceptionIfUpdateTaskDTOIsInvalid() {
        when(taskDao.findById(TASK_ID)).thenReturn(TASK);
        doThrow(new IllegalArgumentException("Invalid update data")).when(taskValidator).validateUpdateTaskDto(UPDATE_TASK_DTO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(UPDATE_TASK_DTO, TASK_ID));
        assertEquals("Invalid update data", exception.getMessage());
        verify(taskDao, never()).update(any(Task.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should get task by ID and return TaskDTO")
    void shouldGetTaskByIdAndReturnTaskDTO() {
        when(taskDao.findById(TASK_ID)).thenReturn(TASK);
        when(taskDtoMapper.mapToDTO(TASK)).thenReturn(TASK_DTO);

        TaskDTO result = taskService.getTaskById(TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        assertEquals(TASK_TITLE, result.getTitle());
        verify(taskDao).findById(TASK_ID);
    }

    @Test
    @DisplayName("Should throw TaskNotFoundException if task not found by ID")
    void shouldThrowTaskNotFoundExceptionIfTaskNotFoundById() {
        when(taskDao.findById(TASK_ID)).thenThrow(new TaskNotFoundException("Task with id " + TASK_ID + " not found"));

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(TASK_ID));
        assertEquals("Task with id " + TASK_ID + " not found", exception.getMessage());
        verify(taskDao).findById(TASK_ID);
    }

    @Test
    @DisplayName("Should get paginated tasks and return list of TaskDTOs")
    void shouldGetPaginatedTasksAndReturnListOfTaskDTOs() {
        int offset = 0;
        int limit = 10;
        List<Task> tasks = List.of(TASK);
        when(taskDao.findTasksWithPagination(offset, limit)).thenReturn(tasks);
        when(taskDtoMapper.mapToDTO(tasks)).thenReturn(List.of(TASK_DTO));

        List<TaskDTO> result = taskService.getTasks(offset, limit);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TASK_ID, result.get(0).getId());
        verify(taskDao).findTasksWithPagination(offset, limit);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if pagination parameters are invalid")
    void shouldThrowIllegalArgumentExceptionIfPaginationParametersAreInvalid() {
        int offset = -1;
        int limit = 0;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTasks(offset, limit));
        assertEquals("Invalid pagination parameters: offset must be >= 0 and limit must be > 0", exception.getMessage());
        verify(taskDao, never()).findTasksWithPagination(offset, limit);
    }

    @Test
    @DisplayName("Should delete task by ID")
    void shouldDeleteTaskById() {
        doNothing().when(taskDao).deleteById(TASK_ID);

        taskService.deleteTask(TASK_ID);

        verify(taskDao).deleteById(TASK_ID);
    }

    @Test
    @DisplayName("Should throw RuntimeException if failed to delete task")
    void shouldThrowRuntimeExceptionIfFailedToDeleteTask() {
        doThrow(new RuntimeException("Failed to delete task")).when(taskDao).deleteById(TASK_ID);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(TASK_ID));
        assertEquals("Failed to delete task", exception.getMessage());
        verify(taskDao).deleteById(TASK_ID);
    }
}