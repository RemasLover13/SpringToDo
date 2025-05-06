package com.emobile.springtodo.mapper;

import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateTaskDtoMapper extends Mappable<Task, CreateTaskDTO> {
}
