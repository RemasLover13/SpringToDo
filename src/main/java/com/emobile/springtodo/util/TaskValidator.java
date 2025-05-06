package com.emobile.springtodo.util;

import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Status;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TaskValidator {

    public void validateUpdateTaskDto(UpdateTaskDto dto) {
        if (dto.getTitle() != null && dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (dto.getDescription() != null && dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }
        if (dto.getStatus() != null) {
            boolean isValidStatus = Arrays.asList(Status.values()).contains(dto.getStatus());
            if (!isValidStatus) {
                throw new IllegalArgumentException("Invalid status value: " + dto.getStatus());
            }
        }
    }
}