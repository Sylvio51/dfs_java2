package com.main.model;

import java.util.UUID;

public class Task {
    private final String id;
    private String title;
    private String description;
    private boolean done;
    private User createdBy;

    public Task(String title, String description, User createdBy) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.done = false;
        this.createdBy = createdBy;
    }

    // Constructeur pour la désérialisation
    public Task(String id, String title, String description, boolean done, User createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", done=" + done +
                ", createdBy=" + createdBy.getFirstName() +
                '}';
    }
} 