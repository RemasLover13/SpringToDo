package com.emobile.springtodo.dto;

import com.emobile.springtodo.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Schema(description = "DTO для обновления задачи")
public class UpdateTaskDto {
    @Schema(description = "Название задачи", example = "Write documentation")
    private String title;
    @Schema(description = "Описание задачи", example = "Write documentation using by hands")
    private String description;
    @Schema(description = "Статус задачи", example = "PENDING")
    private Status status;
}
