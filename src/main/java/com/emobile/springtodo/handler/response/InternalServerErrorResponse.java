package com.emobile.springtodo.handler.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Ответ, который описывает ошибку на стороне сервера")
public class InternalServerErrorResponse {
    @Schema(description = "Сообщение об ошибке", example = "An unexpected error occurred")
    private String message;
    @Schema(description = "Время возникновения ошибки", example = "2025-02-24T12:00:00")
    private LocalDateTime timestamp;
}
