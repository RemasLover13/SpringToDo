package com.emobile.springtodo.unit.controller;

import com.emobile.springtodo.controller.TaskRestController;
import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskRestController.class)
@DisplayName("Unit tests for TaskRestController")
class TaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    private static final Long TASK_ID = 1L;
    private static final String TASK_TITLE = "Add springboot-starter";
    private static final String TASK_DESCRIPTION = "This simplify development";
    private static final Status TASK_STATUS = Status.PENDING;
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

    private final TaskDTO TASK_DTO = TaskDTO.builder()
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

    @Test
    @DisplayName("Should return paginated tasks")
    void shouldReturnPaginatedTasks() throws Exception {
        int offset = 0;
        int limit = 10;
        List<TaskDTO> tasks = List.of(TASK_DTO);
        when(taskService.getTasks(offset, limit)).thenReturn(tasks);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks")
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(TASK_ID))
                .andExpect(jsonPath("$[0].title").value(TASK_TITLE));

        verify(taskService).getTasks(offset, limit);
    }


    @Test
    @DisplayName("Should return task by ID")
    void shouldReturnTaskById() throws Exception {
        when(taskService.getTaskById(TASK_ID)).thenReturn(TASK_DTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{id}", TASK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TASK_ID))
                .andExpect(jsonPath("$.title").value(TASK_TITLE));

        verify(taskService).getTaskById(TASK_ID);
    }

    @Test
    @DisplayName("Should throw NotFound if task not found by ID")
    void shouldThrowNotFoundIfTaskNotFoundById() throws Exception {
        when(taskService.getTaskById(TASK_ID)).thenThrow(new TaskNotFoundException("Task with id " + TASK_ID + " not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/{id}", TASK_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id " + TASK_ID + " not found"));

        verify(taskService).getTaskById(TASK_ID);
    }

    @Test
    @DisplayName("Should create a new task")
    void shouldCreateNewTask() throws Exception {
        when(taskService.createTask(any(CreateTaskDTO.class))).thenReturn(TASK_DTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(CREATE_TASK_DTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TASK_ID))
                .andExpect(jsonPath("$.title").value(TASK_TITLE));

        verify(taskService).createTask(any(CreateTaskDTO.class));
    }

    @Test
    @DisplayName("Should throw BadRequest if createTaskDTO is invalid")
    void shouldThrowBadRequestIfCreateTaskDTOIsInvalid() throws Exception {
        CreateTaskDTO invalidDto = new CreateTaskDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(CreateTaskDTO.class));
    }

    @Test
    @DisplayName("Should update an existing task")
    void shouldUpdateExistingTask() throws Exception {
        when(taskService.updateTask(any(UpdateTaskDto.class), eq(TASK_ID))).thenReturn(TASK_DTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/{id}", TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(UPDATE_TASK_DTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TASK_ID))
                .andExpect(jsonPath("$.title").value(TASK_TITLE));

        verify(taskService).updateTask(any(UpdateTaskDto.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should throw NotFound if task not found during update")
    void shouldThrowNotFoundIfTaskNotFoundDuringUpdate() throws Exception {
        when(taskService.updateTask(any(UpdateTaskDto.class), eq(TASK_ID)))
                .thenThrow(new TaskNotFoundException("Task with id " + TASK_ID + " not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/{id}", TASK_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(UPDATE_TASK_DTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id " + TASK_ID + " not found"));

        verify(taskService).updateTask(any(UpdateTaskDto.class), eq(TASK_ID));
    }

    @Test
    @DisplayName("Should delete task by ID")
    void shouldDeleteTaskById() throws Exception {
        doNothing().when(taskService).deleteTask(TASK_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/{id}", TASK_ID))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(TASK_ID);
    }

    @Test
    @DisplayName("Should throw NotFound if task not found during delete")
    void shouldThrowNotFoundIfTaskNotFoundDuringDelete() throws Exception {
        doThrow(new TaskNotFoundException("Task with id " + TASK_ID + " not found")).when(taskService).deleteTask(TASK_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/{id}", TASK_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task with id " + TASK_ID + " not found"));

        verify(taskService).deleteTask(TASK_ID);
    }

    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}
