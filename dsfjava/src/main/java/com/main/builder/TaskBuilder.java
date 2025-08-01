package com.main.builder;

import com.main.model.Task;
import com.main.model.DatedTask;
import com.main.model.User;

import java.time.LocalDate;

public class TaskBuilder {
    private String title;
    private String description;
    private User createdBy;
    private boolean done = false;
    private LocalDate dueDate;
    private boolean isDatedTask = false;

    public TaskBuilder() {
        // Constructeur par défaut
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder createdBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public TaskBuilder done(boolean done) {
        this.done = done;
        return this;
    }

    public TaskBuilder dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.isDatedTask = true;
        return this;
    }

    public TaskBuilder dueDate(String dueDateStr) {
        try {
            this.dueDate = LocalDate.parse(dueDateStr);
            this.isDatedTask = true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format yyyy-MM-dd");
        }
        return this;
    }

    public Task build() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalStateException("Le titre est obligatoire");
        }
        if (createdBy == null) {
            throw new IllegalStateException("L'utilisateur créateur est obligatoire");
        }

        if (isDatedTask && dueDate != null) {
            DatedTask datedTask = new DatedTask(title, description, createdBy, dueDate);
            datedTask.setDone(done);
            return datedTask;
        } else {
            Task task = new Task(title, description, createdBy);
            task.setDone(done);
            return task;
        }
    }

    // Méthodes utilitaires pour créer des tâches rapidement
    public static TaskBuilder simpleTask() {
        return new TaskBuilder();
    }

    public static TaskBuilder datedTask() {
        TaskBuilder builder = new TaskBuilder();
        builder.isDatedTask = true;
        return builder;
    }

    public static Task createSimpleTask(String title, String description, User createdBy) {
        return new TaskBuilder()
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .build();
    }

    public static DatedTask createDatedTask(String title, String description, User createdBy, LocalDate dueDate) {
        return (DatedTask) new TaskBuilder()
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .dueDate(dueDate)
                .build();
    }

    public static DatedTask createDatedTask(String title, String description, User createdBy, String dueDateStr) {
        return (DatedTask) new TaskBuilder()
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .dueDate(dueDateStr)
                .build();
    }
} 