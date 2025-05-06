package com.emobile.springtodo.swagger;

import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.handler.response.ErrorResponse;
import com.emobile.springtodo.handler.response.InternalServerErrorResponse;
import com.emobile.springtodo.handler.response.TaskNotFoundErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Task Management", description = "API for managing tasks")
public interface TaskControllerApi {

    @Operation(
            summary = "Get all tasks with pagination",
            description = "Returns a paginated list of tasks",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid pagination parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"message\": \"Invalid pagination parameters: offset must be >= 0 and limit must be > 0\",\n" +
                                                    "  \"timestamp\": \"2025-04-28T12:47:57.8860753\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InternalServerErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    List<TaskDTO> getTasks(
            @Parameter(description = "Pagination offset", example = "0")
            @RequestParam(defaultValue = "0", required = false) int offset,

            @Parameter(description = "Number of tasks per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) int limit
    );

    @Operation(
            summary = "Get task by ID",
            description = "Returns a single task by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskNotFoundErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"message\": \"Task with id 100 not found\",\n" +
                                                    "  \"timestamp\": \"2025-04-28T12:47:57.8860753\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InternalServerErrorResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    TaskDTO getTask(
            @Parameter(description = "ID of the task to retrieve", example = "1")
            @PathVariable Long id
    );

    @Operation(
            summary = "Create a new task",
            description = "Creates a new task and returns it",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Task created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"message\": \"Invalid input data\",\n" +
                                                    "  \"timestamp\": \"2025-04-28T09:24:01.178Z\",\n" +
                                                    "  \"errors\": {\n" +
                                                    "    \"title\": \"Title should not be empty\",\n" +
                                                    "    \"status\": \"Status should not be empty. Accessible: PENDING, IN_PROGRESS, COMPLETED\"\n" +
                                                    "  }\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InternalServerErrorResponse.class)
                            )
                    )

            }
    )
    @PostMapping
    TaskDTO createTask(
            @Parameter(description = "Task data to create", required = true)
            @RequestBody @Valid CreateTaskDTO taskDTO
    );

    @Operation(
            summary = "Update an existing task",
            description = "Updates an existing task by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Task updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation failed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskNotFoundErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"message\": \"Task with id 100 not found\",\n" +
                                                    "  \"timestamp\": \"2025-04-28T12:47:57.8860753\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InternalServerErrorResponse.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    TaskDTO updateTask(
            @Parameter(description = "Task data to update", required = true)
            @RequestBody @Valid UpdateTaskDto taskDTO,
            @Parameter(description = "ID of the task to update", example = "1")
            @PathVariable Long id
    );

    @Operation(
            summary = "Delete a task",
            description = "Deletes a task by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Task not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskNotFoundErrorResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"message\": \"Task with id 100 not found\",\n" +
                                                    "  \"timestamp\": \"2025-04-28T12:47:57.8860753\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InternalServerErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    void deleteTask(
            @Parameter(description = "ID of the task to delete", example = "1")
            @PathVariable Long id
    );
}