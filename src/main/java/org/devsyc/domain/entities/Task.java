package org.devsyc.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.devsyc.domain.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ElementCollection
    @CollectionTable(name = "task_tags", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "tag")
    private List<String> tags;

    public Task() {
    }

    public Task(String title, String description, LocalDateTime dueDate, User assignedUser, User createdBy, List<String> tags) {
        this.title = title;
        this.description = description;
        this.creationDate = LocalDateTime.now();
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
        this.assignedUser = assignedUser;
        this.createdBy = createdBy;
        this.tags = tags;
    }
}