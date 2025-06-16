package com.taskmanager.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;  // new status field
    
    private LocalDate setDate;

    @ManyToOne(fetch = FetchType.LAZY)
     @JsonIgnore
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private boolean complete = false;  // new complete field with default false

    public Task() {}

    public Task(Long id, String title, String description, AppUser user, TaskStatus status, LocalDate setDate, boolean complete) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.user = user;
        this.status = status;
        this.setDate = setDate;
        this.complete = complete;
    }

    // Getters and setters
    
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getSetDate() {
        return setDate;
    }

    public void setSetDate(LocalDate setDate) {
        this.setDate = setDate;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}



