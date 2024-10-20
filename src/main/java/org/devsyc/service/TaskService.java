package org.devsyc.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.devsyc.domain.entities.Task;
import org.devsyc.domain.entities.TaskRequest;
import org.devsyc.domain.entities.User;
import org.devsyc.domain.enums.RequestStatus;
import org.devsyc.domain.enums.RequestType;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.dto.TaskDTO;
import org.devsyc.repository.TaskRepositoryHibernate;
import org.devsyc.repository.TaskRequestRepositoryHibernate;
import org.devsyc.repository.UserRepositoryHibernate;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskService {
    private TaskRepositoryHibernate taskRepository;
    private UserRepositoryHibernate userRepository;
    private TaskRequestRepositoryHibernate taskRequestRepositoryHibernate;


    public TaskService() {
        this.taskRepository = new TaskRepositoryHibernate();
        this.userRepository = new UserRepositoryHibernate();
        this.taskRequestRepositoryHibernate = new TaskRequestRepositoryHibernate();

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
        System.out.println("Tasks found for user " + userId + ": " + tasks.size());
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

        List<TaskRequest> pendingRequests = taskRequestRepositoryHibernate.findPendingRequestsForTask(task.getId());

        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getTags(),
                assignedUserName,
                pendingRequests
        );
    }

    public void requestEditTask(Long taskId, Long userId) throws IllegalStateException {
        Task task = taskRepository.findById(taskId);
        User user = userRepository.findById(userId);

        if (task == null || user == null) {
            throw new IllegalStateException("Task or User not found");
        }

        /*if (task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot request to edit your own task.");
        }*/

        if (user.getReplacementTokens() <= 0) {
            throw new IllegalStateException("Not enough replacement tokens available.");
        }

        TaskRequest request = new TaskRequest();
        request.setTask(task);
        request.setRequestedBy(user);
        request.setStatus(RequestStatus.PENDING);
        request.setType(RequestType.EDIT);
        request.setRequestedAt(LocalDateTime.now());

        taskRequestRepositoryHibernate.save(request);

        user.setReplacementTokens(user.getReplacementTokens() - 1);
        userRepository.update(user);
    }

    public void requestDeleteTask(Long taskId, Long userId) throws IllegalStateException {
        Task task = taskRepository.findById(taskId);
        User user = userRepository.findById(userId);

        if (task == null || user == null) {
            throw new IllegalStateException("Task or User not found");
        }

        /*if (task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalStateException("You cannot request to delete your own task.");
        }*/


        if (user.getDeletionTokens() <= 0) {
            throw new IllegalStateException("Not enough deletion tokens available.");
        }

        TaskRequest request = new TaskRequest();
        request.setTask(task);
        request.setRequestedBy(user);
        request.setStatus(RequestStatus.PENDING);
        request.setType(RequestType.DELETE);
        request.setRequestedAt(LocalDateTime.now());

        taskRequestRepositoryHibernate.save(request);

        user.setDeletionTokens(user.getDeletionTokens() - 1);
        userRepository.update(user);
    }
}