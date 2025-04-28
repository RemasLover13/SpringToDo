package com.emobile.springtodo.dto;

import com.emobile.springtodo.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Schema(description = "DTO для представления задачи")
public class TaskDTO {

    @Schema(description = "id задачи", example = "2")
    private Long id;

    @Schema(description = "Название задачи", example = "Write documentation")
    private String title;

    @Schema(description = "Описание задачи", example = "Write documentation using by hands")
    private String description;

    @Schema(description = "Время создания задачи", example = "2025-04-28T09:24:01.178Z")
    private LocalDateTime createdAt;

    @Schema(description = "Время обновления задачи", example = "2025-04-28T09:24:01.179Z")
    private LocalDateTime updatedAt;

    @Schema(description = "Статус задачи", example = "PENDING")
    private Status status;
}