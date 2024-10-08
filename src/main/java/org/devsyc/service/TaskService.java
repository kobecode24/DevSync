package org.devsyc.service;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(Task task) {
        validateTask(task);
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        validateTask(task);
        taskRepository.update(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public void markTaskAsDone(Task task) {
        if (task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Cannot mark a task as done after its due date");
        }
        task.setStatus(TaskStatus.DONE);
        task.setCompletionDate(LocalDateTime.now());
        taskRepository.update(task);
    }

    public void updateOverdueTasks() {
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        for (Task task : overdueTasks) {
            task.setStatus(TaskStatus.TODO);
            taskRepository.update(task);
        }
    }

    private void validateTask(Task task) {
        if (task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Task due date cannot be in the past");
        }
        if (task.getDueDate().isAfter(LocalDateTime.now().plusDays(3))) {
            throw new IllegalArgumentException("Task cannot be scheduled more than 3 days in advance");
        }
        if (task.getTags() == null || task.getTags().size() < 2) {
            throw new IllegalArgumentException("Task must have at least 2 tags");
        }
    }
}