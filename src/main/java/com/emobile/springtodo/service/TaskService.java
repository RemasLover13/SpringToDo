package com.emobile.springtodo.service;

import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;

import java.util.List;

public interface TaskService {

    TaskDTO createTask(CreateTaskDTO createTaskDTO);

    TaskDTO updateTask(UpdateTaskDto updateTaskDTO, Long id);

    TaskDTO getTaskById(Long id);

    List<TaskDTO> getTasks(int offset, int limit);

    void deleteTask(Long id);

}
