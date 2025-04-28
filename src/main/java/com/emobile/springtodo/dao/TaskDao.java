package com.emobile.springtodo.dao;

import com.emobile.springtodo.entity.Task;

import java.util.List;

public interface TaskDao {

    List<Task> findTasksWithPagination(int offset, int limit);

    Task findById(Long id);

    void deleteById(Long id);

    Task save(Task task);

    Task update(Task task, Long id);

}
