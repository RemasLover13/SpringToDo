package com.emobile.springtodo.handler.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Ответ с ошибкой общего типа")
public class ErrorResponse {
    @Schema(description = "Сообщение об ошибке", example = "Some error occurred")
    private String message;
    @Schema(description = "Время возникновения ошибки", example = "2025-02-24T12:00:00")
    private LocalDateTime timestamp;
    @Schema(description = "Ошибки в конкретных полях", example = "status: Status should not be empty. Accessible: PENDING, IN_PROGRESS, COMPLETED")
    private Map<String, String> errors;

    public ErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
