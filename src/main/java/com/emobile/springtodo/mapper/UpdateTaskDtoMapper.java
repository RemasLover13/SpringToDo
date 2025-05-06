package com.emobile.springtodo.mapper;

import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateTaskDtoMapper extends Mappable<Task, UpdateTaskDto> {
}
