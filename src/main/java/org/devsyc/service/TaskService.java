package org.devsyc.service;

import org.devsyc.domain.entities.Task;
import org.devsyc.domain.enums.TaskStatus;
import org.devsyc.dto.TaskDTO;
import org.devsyc.repository.TaskRepositoryHibernate;

import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private TaskRepositoryHibernate taskRepository;

    public TaskService() {
        this.taskRepository = new TaskRepositoryHibernate();
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
        taskRepository.save(task);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public boolean deleteTask(Long id) {
        Task task = taskRepository.findById(id);
        if (task != null) {
            taskRepository.delete(task);
            return true;
        }
        return false;
    }

    public List<TaskDTO> getTasksForUser(long userId) {
        List<Task> tasks = taskRepository.findByAssignedUserId(userId);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public void updateTaskStatus(long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            taskRepository.update(task);
        }
    }

    private TaskDTO convertToDTO(Task task) {
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
