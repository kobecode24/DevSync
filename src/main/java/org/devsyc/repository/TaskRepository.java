package org.devsyc.repository;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository {
    void save(Task task);
    Task findById(Long id);
    List<Task> findAll();
    List<Task> findByUser(User user);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findOverdueTasks(LocalDateTime currentDate);
    void update(Task task);
    void delete(Task task);
}