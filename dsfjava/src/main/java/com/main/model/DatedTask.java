package com.main.model;

import java.time.LocalDate;

public class DatedTask extends Task {
    private LocalDate dueDate;

    public DatedTask(String title, String description, User createdBy, LocalDate dueDate) {
        super(title, description, createdBy);
        this.dueDate = dueDate;
    }

    // Constructeur pour la désérialisation
    public DatedTask(String id, String title, String description, boolean done, User createdBy, LocalDate dueDate) {
        super(id, title, description, done, createdBy);
        this.dueDate = dueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "DatedTask{" +
                "id='" + getId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", done=" + isDone() +
                ", createdBy=" + getCreatedBy().getFirstName() +
                ", dueDate=" + dueDate +
                '}';
    }
} 