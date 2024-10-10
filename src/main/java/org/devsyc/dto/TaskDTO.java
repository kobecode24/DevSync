package org.devsyc.dto;

import lombok.Getter;
import lombok.Setter;
import org.devsyc.domain.entities.TaskRequest;
import org.devsyc.domain.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private List<String> tags;
    private String assignedUserName;
    private List<TaskRequest> pendingRequests;


    public TaskDTO(Long id, String title, String description, LocalDateTime dueDate,
                   TaskStatus status, List<String> tags, String assignedUserName , List<TaskRequest> pendingRequests) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.tags = tags;
        this.assignedUserName = assignedUserName;
        this.pendingRequests = pendingRequests;
    }

}