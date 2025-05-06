package com.emobile.springtodo.dto;

import com.emobile.springtodo.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Schema(description = "DTO для создания задачи")
public class CreateTaskDTO {
    @NotEmpty(message = "Title should not be empty")
    @Schema(description = "Название задачи", example = "Write documentation")
    private String title;
    @Schema(description = "Описание задачи", example = "Write documentation using by hands")
    private String description;
    @NotNull(message = "Status should not be empty. Accessible: PENDING, IN_PROGRESS, COMPLETED")
    @Schema(description = "Статус задачи", example = "PENDING")
    private Status status;
}
