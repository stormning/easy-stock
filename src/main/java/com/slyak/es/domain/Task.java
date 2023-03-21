package com.slyak.es.domain;

import lombok.Data;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "task")
@Data
public class Task extends AbstractPersistable<Long> {

    private String title;

    private String content;

    private Date dueDate;

    private Date completedAt;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;

    private String relatedEntityType;

    private Long relatedEntityId;

    // Getters and setters
    // ...

    public void complete() {
        this.setStatus(TaskStatus.COMPLETED);
        // TODO: update related entity status or other properties
    }
}
