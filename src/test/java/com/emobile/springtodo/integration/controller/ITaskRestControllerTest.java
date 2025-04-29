package com.emobile.springtodo.integration.controller;

import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Status;
import com.emobile.springtodo.integration.config.TestContainerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@DisplayName("Integration tests for TaskRestController")
class ITaskRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DirtiesContext
    @DisplayName("Should get task by ID and return 200 OK")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldGetTaskByIdAndReturn200OK() throws Exception {
        String expectedJson = """
                {
                  "title": "Updated Title",
                  "description": "Updated Description",
                  "status": "COMPLETED"
                }
                """;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @DirtiesContext
    @DisplayName("Should create a new task and return 201 Created")
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldCreateTaskAndReturn201Created() throws Exception {
        CreateTaskDTO createTaskDTO = CreateTaskDTO.builder()
                .title("New Task")
                .description("New Description")
                .status(Status.PENDING)
                .build();

        String expectedJson = """
                {
                  "id": 3,
                  "title": "New Task",
                  "description": "New Description",
                  "status": "PENDING"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @DisplayName("Should get all tasks with pagination and return 200 OK")
    @DirtiesContext
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldGetAllTasksWithPaginationAndReturn200OK() throws Exception {
        String expectedJson = """
                [
                  {
                    "id": 1,
                    "title": "Task 1",
                    "description": "Description 1",
                    "status": "PENDING"
                  },
                  {
                    "id": 2,
                    "title": "Task 2",
                    "description": "Description 2",
                    "status": "COMPLETED"
                  }
                ]
                """;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks")
                        .param("offset", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @DisplayName("Should return 404 Not Found if task does not exist")
    @DirtiesContext
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldReturn404NotFoundIfTaskDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String actualJson = result.getResponse().getContentAsString();
                    String expectedJson = """
                            {
                              "message": "Task with id 999 not found"
                            }
                            """;
                    JSONAssert.assertEquals(expectedJson, actualJson, false);
                });
    }

    @Test
    @DisplayName("Should update task and return 200 OK")
    @DirtiesContext
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldUpdateTaskAndReturn200OK() throws Exception {
        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(Status.COMPLETED)
                .build();

        String expectedJson = """
                {
                  "title": "Updated Title",
                  "description": "Updated Description",
                  "status": "COMPLETED"
                }
                """;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTaskDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualJson = result.getResponse().getContentAsString();
        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @DisplayName("Should delete task and return 204 No Content")
    @DirtiesContext
    @Sql(scripts = {"/data/schema.sql", "/data/data.sql"})
    void shouldDeleteTaskAndReturn204NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/1"))
                .andExpect(status().isNotFound());
    }
}