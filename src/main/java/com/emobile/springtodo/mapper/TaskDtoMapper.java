package com.emobile.springtodo.mapper;

import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskDtoMapper extends Mappable<Task, TaskDTO> {
}
