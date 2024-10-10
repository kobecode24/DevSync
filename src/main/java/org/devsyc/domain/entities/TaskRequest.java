package org.devsyc.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.devsyc.domain.enums.RequestStatus;
import org.devsyc.domain.enums.RequestType;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "task_requests")
public class TaskRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "processed_by")
    private User processedBy;

}

