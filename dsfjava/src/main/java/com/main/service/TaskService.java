package com.main.service;

import com.main.model.Task;
import com.main.model.DatedTask;
import com.main.model.User;
import com.main.database.DatabaseAccess;
import com.main.builder.TaskBuilder;
import com.main.exception.ElementNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class TaskService {
    private DatabaseAccess database;

    public TaskService() {
        this.database = DatabaseAccess.getInstance();
    }

    // Lister toutes les tâches
    public List<Task> getAllTasks() {
        return database.getAllTasks();
    }

    // Lister les tâches d'un utilisateur
    public List<Task> getTasksByUser(User user) {
        return database.getTasksByUser(user);
    }

    // Lister les tâches avec échéance
    public List<DatedTask> getAllDatedTasks() {
        return database.getAllDatedTasks();
    }

    // Créer une tâche simple avec TaskBuilder
    public Task createTask(String title, String description, User createdBy) {
        Task task = TaskBuilder.createSimpleTask(title, description, createdBy);
        database.addTask(task);
        return task;
    }

    // Créer une tâche avec échéance avec TaskBuilder
    public DatedTask createDatedTask(String title, String description, User createdBy, LocalDate dueDate) {
        DatedTask task = TaskBuilder.createDatedTask(title, description, createdBy, dueDate);
        database.addTask(task);
        return task;
    }

    // Créer une tâche avec échéance avec TaskBuilder (version String)
    public DatedTask createDatedTask(String title, String description, User createdBy, String dueDateStr) {
        DatedTask task = TaskBuilder.createDatedTask(title, description, createdBy, dueDateStr);
        database.addTask(task);
        return task;
    }

    // Trouver une tâche par son ID
    public Task getTaskById(String id) throws ElementNotFoundException {
        return database.findTaskById(id);
    }

    // Modifier une tâche
    public void updateTask(String id, String title, String description, boolean done) throws ElementNotFoundException {
        database.updateTask(id, title, description, done);
    }

    // Modifier une tâche avec échéance
    public void updateDatedTask(String id, String title, String description, boolean done, LocalDate dueDate) throws ElementNotFoundException {
        database.updateDatedTask(id, title, description, done, dueDate);
    }

    // Marquer une tâche comme terminée
    public void markTaskAsDone(String id) throws ElementNotFoundException {
        database.markTaskAsDone(id);
    }

    // Marquer une tâche comme non terminée
    public void markTaskAsUndone(String id) throws ElementNotFoundException {
        database.markTaskAsUndone(id);
    }

    // Supprimer une tâche
    public void deleteTask(String id) throws ElementNotFoundException {
        database.deleteTask(id);
    }

    // Supprimer toutes les tâches d'un utilisateur
    public void deleteAllTasksByUser(User user) {
        database.deleteAllTasksByUser(user);
    }

    // Obtenir les tâches en retard
    public List<DatedTask> getOverdueTasks() {
        return database.getOverdueTasks();
    }

    // Obtenir les tâches à venir
    public List<DatedTask> getUpcomingTasks() {
        return database.getUpcomingTasks();
    }

    // Compter les tâches totales
    public int getTotalTaskCount() {
        return database.getTaskCount();
    }

    // Compter les tâches terminées
    public int getCompletedTaskCount() {
        return database.getCompletedTaskCount();
    }

    // Compter les tâches non terminées
    public int getPendingTaskCount() {
        return database.getPendingTaskCount();
    }

    // Méthode pour créer une tâche avec le pattern Builder
    public Task createTaskWithBuilder(TaskBuilder builder) {
        Task task = builder.build();
        database.addTask(task);
        return task;
    }
} 