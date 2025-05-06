package com.emobile.springtodo.service;

import com.emobile.springtodo.dao.TaskDao;
import com.emobile.springtodo.dto.CreateTaskDTO;
import com.emobile.springtodo.dto.TaskDTO;
import com.emobile.springtodo.dto.UpdateTaskDto;
import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.mapper.CreateTaskDtoMapper;
import com.emobile.springtodo.mapper.TaskDtoMapper;
import com.emobile.springtodo.mapper.UpdateTaskDtoMapper;
import com.emobile.springtodo.util.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;
    private final CreateTaskDtoMapper createTaskDtoMapper;
    private final UpdateTaskDtoMapper updateTaskDtoMapper;
    private final TaskDtoMapper taskDtoMapper;
    private final TaskValidator taskValidator;


    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "tasks", key = "#result.id"),
                    @CacheEvict(value = "all-tasks", allEntries = true),
            }
    )
    public TaskDTO createTask(CreateTaskDTO createTaskDTO) {
        Task task = createTaskDtoMapper.mapToEntity(createTaskDTO);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        Task save = taskDao.save(task);
        return taskDtoMapper.mapToDTO(save);

    }

    @Transactional
    @Override
    @Caching(
            put = @CachePut(value = "tasks", key = "#id"),
            evict = @CacheEvict(value = "all-tasks", allEntries = true)
    )
    public TaskDTO updateTask(UpdateTaskDto updateTaskDTO, Long id) {
        Task taskById = taskDao.findById(id);
        if (taskById == null) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }

        taskValidator.validateUpdateTaskDto(updateTaskDTO);

        taskById.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = updateTaskDtoMapper.mapToEntity(updateTaskDTO);
        Task savedTask = taskDao.update(updatedTask, id);

        return taskDtoMapper.mapToDTO(savedTask);
    }

    @Override
    @Cacheable(cacheNames = "tasks", key = "#id")
    public TaskDTO getTaskById(Long id) {
        Task task = taskDao.findById(id);
        return taskDtoMapper.mapToDTO(task);
    }

    @Override
    @Cacheable(cacheNames = "all-tasks", key = "'pagination' + #offset + ':' + #limit")
    public List<TaskDTO> getTasks(int offset, int limit) {
        if (offset < 0 || limit <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: offset must be >= 0 and limit must be > 0");
        }
        return taskDtoMapper.mapToDTO(taskDao.findTasksWithPagination(offset, limit));
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "tasks", key = "#id"),
            @CacheEvict(value = "all-tasks", allEntries = true)
    })
    public void deleteTask(Long id) {
        taskDao.deleteById(id);
    }
}
