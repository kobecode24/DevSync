package org.devsyc.dto;

import org.devsyc.domain.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private List<String> tags;
    private String assignedUserName;

    public TaskDTO(Long id, String title, String description, LocalDateTime dueDate,
                   TaskStatus status, List<String> tags, String assignedUserName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.tags = tags;
        this.assignedUserName = assignedUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }
}