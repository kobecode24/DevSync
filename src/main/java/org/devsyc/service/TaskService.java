package org.devsyc.service;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.dto.TaskDTO;
import org.devsyc.repository.TaskRepositoryHibernate;
import org.devsyc.repository.UserRepositoryHibernate;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private TaskRepositoryHibernate taskRepository;
    private UserRepositoryHibernate userRepository;

    public TaskService() {
        this.taskRepository = new TaskRepositoryHibernate();
        this.userRepository = new UserRepositoryHibernate();
    }

    public List<TaskDTO> getAllTaskDTOs() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public TaskDTO getTaskDTOById(Long id) {
        Task task = taskRepository.findById(id);
        return task != null ? convertToDTO(task) : null;
    }

    public void createTask(Task task) {
        validateTask(task);
        task.setCreationDate(LocalDateTime.now());
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        validateTask(task);
        taskRepository.update(task);
    }

    public boolean deleteTask(Long taskId, Long userId) throws IllegalStateException {
        Task task = taskRepository.findById(taskId);
        User user = userRepository.findById(userId);

        if (task == null || user == null) {
            return false;
        }

        if (task.getCreatedBy().getId().equals(userId)) {
            taskRepository.delete(task);
        } else if (user.useDeletionToken()) {
            taskRepository.delete(task);
            userRepository.update(user);
        } else {
            throw new IllegalStateException("No deletion tokens available.");
        }
        return true;
    }

    public List<TaskDTO> getTasksForUser(long userId) {
        List<Task> tasks = taskRepository.findByAssignedUserId(userId);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<TaskDTO> getAvailableTasks() {
        List<Task> tasks = taskRepository.findByAssignedUserIsNull();
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void updateTaskStatus(long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId);
        if (task != null) {
            if (newStatus == TaskStatus.DONE && task.getDueDate().isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("Cannot mark task as complete after the deadline");
            }
            task.setStatus(newStatus);
            taskRepository.update(task);
        }
    }

    public void assignTaskToSelf(Long taskId, Long userId) throws IllegalStateException {
        Task task = taskRepository.findById(taskId);
        User user = userRepository.findById(userId);

        if (task == null || user == null) {
            throw new IllegalStateException("Task or User not found");
        }

        if (task.getAssignedUser() != null) {
            throw new IllegalStateException("This task is already assigned.");
        }

        task.setAssignedUser(user);
        taskRepository.update(task);
    }

    public void replaceTask(Long taskId, Long userId) throws IllegalStateException {
        Task task = taskRepository.findById(taskId);
        User user = userRepository.findById(userId);

        if (task == null || user == null) {
            throw new IllegalStateException("Task or User not found");
        }

        if (task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot replace a task assigned to yourself.");
        }

        if (user.useReplacementToken()) {
            task.setAssignedUser(user);
            taskRepository.update(task);
            userRepository.update(user);
        } else {
            throw new IllegalStateException("No replacement tokens available.");
        }
    }

    private void validateTask(Task task) {
        LocalDateTime now = LocalDateTime.now();

        if (task.getDueDate().isBefore(now)) {
            throw new IllegalArgumentException("Task due date cannot be in the past");
        }

        if (task.getDueDate().isAfter(now.plusDays(3))) {
            throw new IllegalArgumentException("Task cannot be scheduled more than 3 days in advance");
        }

        if (task.getTags() == null || task.getTags().size() < 2) {
            throw new IllegalArgumentException("Task must have at least 2 tags");
        }
    }

    private TaskDTO convertToDTO(Task task) {
        Hibernate.initialize(task.getTags());
        String assignedUserName = task.getAssignedUser() != null
                ? task.getAssignedUser().getFirstName() + " " + task.getAssignedUser().getLastName()
                : "Unassigned";

        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getTags(),
                assignedUserName
        );
    }
}
