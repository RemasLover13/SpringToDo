package com.emobile.springtodo.dao;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskDaoImpl implements TaskDao {

    private final JdbcTemplate jdbcTemplate;
    private static final RowMapper<Task> taskRowMapper = new BeanPropertyRowMapper<>(Task.class);

    private static final String FIND_BY_ID = "select * from tasks where id = ?";
    private static final String DELETE_BY_ID = "delete from tasks where id = ?";
    private static final String SAVE_TASK = "insert into tasks(title, description, status, created_at, updated_at) values(?,?,?,?,?)";
    private static final String FIND_ALL_WITH_PAGINATION = "select * from tasks limit ? offset ?";

    @Override
    public List<Task> findTasksWithPagination(int offset, int limit) {
        log.info("Find tasks with pagination {}, {}", offset, limit);
        return jdbcTemplate.query(FIND_ALL_WITH_PAGINATION, taskRowMapper, limit, offset);
    }

    @Override
    public Task findById(Long id) {
        log.info("Find task by id {}", id);
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID, taskRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Delete task by id {}", id);
        int update = jdbcTemplate.update(DELETE_BY_ID, id);
        if (update != 1) {
            log.error("Failed to delete task by id {}", id);
            throw new RuntimeException("Failed to delete task by id " + id);
        }
    }

    @Override
    public Task save(Task task) {
        if (task == null || task.getTitle() == null || task.getStatus() == null) {
            throw new IllegalArgumentException("Task data is invalid");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SAVE_TASK, new String[]{"id"});
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setString(3, task.getStatus().name());
            ps.setTimestamp(4, Timestamp.valueOf(task.getCreatedAt()));
            ps.setTimestamp(5, Timestamp.valueOf(task.getUpdatedAt()));
            return ps;
        }, keyHolder);

        if (update < 0) {
            throw new RuntimeException("Failed to save task");
        }

        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            task.setId(generatedId.longValue());
        } else {
            throw new RuntimeException("Failed to retrieve generated ID for the task");
        }
        log.info("Save task {}", task);
        return task;
    }

    @Override
    public Task update(Task task, Long id) {
        log.info("Update task {} with id {}", task, id);

        if (task == null || id == null) {
            throw new IllegalArgumentException("Task or ID cannot be null");
        }

        StringBuilder sql = new StringBuilder("UPDATE tasks SET ");
        List<Object> params = new ArrayList<>();

        boolean hasUpdates = false;

        if (task.getTitle() != null) {
            sql.append("title = ?, ");
            params.add(task.getTitle());
            hasUpdates = true;
        }

        if (task.getDescription() != null) {
            sql.append("description = ?, ");
            params.add(task.getDescription());
            hasUpdates = true;
        }

        if (task.getStatus() != null) {
            sql.append("status = ?, ");
            params.add(task.getStatus().name());
            hasUpdates = true;
        }

        if (!hasUpdates) {
            throw new IllegalArgumentException("No fields to update");
        }

        sql.setLength(sql.length() - 2);
        sql.append(" WHERE id = ?");
        params.add(id);

        int rowsUpdated = jdbcTemplate.update(sql.toString(), params.toArray());

        if (rowsUpdated == 0) {
            throw new TaskNotFoundException("Task with id " + id + " not found");
        }

        return task;
    }
}
