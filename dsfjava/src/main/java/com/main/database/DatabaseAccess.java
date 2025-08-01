package com.main.database;

import com.main.model.User;
import com.main.model.Task;
import com.main.model.DatedTask;
import com.main.exception.ElementNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {
    private static DatabaseAccess instance;
    private List<User> users;
    private List<Task> tasks;

    // Constructeur privé pour le pattern Singleton
    private DatabaseAccess() {
        this.users = new ArrayList<>();
        this.tasks = new ArrayList<>();
        initializeWithSampleData();
    }

    // Méthode pour obtenir l'instance unique (Singleton)
    public static synchronized DatabaseAccess getInstance() {
        if (instance == null) {
            instance = new DatabaseAccess();
        }
        return instance;
    }

    // Initialisation avec des données d'exemple
    private void initializeWithSampleData() {
        // Créer quelques utilisateurs d'exemple
        User user1 = new User("Alice");
        User user2 = new User("Bob");
        User user3 = new User("Charlie");

        users.add(user1);
        users.add(user2);
        users.add(user3);

        // Créer quelques tâches d'exemple
        Task task1 = new Task("Apprendre Java", "Étudier les concepts de base de Java", user1);
        Task task2 = new Task("Faire les courses", "Acheter du pain et du lait", user2);
        DatedTask task3 = new DatedTask("Rendre le projet", "Terminer le projet TODO List", user1, LocalDate.now().plusDays(7));
        DatedTask task4 = new DatedTask("Réunion équipe", "Préparer la présentation", user3, LocalDate.now().plusDays(2));

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
    }

    // === MÉTHODES POUR LES UTILISATEURS ===

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User findUserById(String id) throws ElementNotFoundException {
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
        
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ElementNotFoundException("Utilisateur avec l'ID '" + id + "' non trouvé");
        }
    }

    public User findUserByFirstName(String firstName) throws ElementNotFoundException {
        Optional<User> user = users.stream()
                .filter(u -> u.getFirstName().equals(firstName))
                .findFirst();
        
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ElementNotFoundException("Utilisateur avec le prénom '" + firstName + "' non trouvé");
        }
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void updateUser(String id, String newFirstName) throws ElementNotFoundException {
        User user = findUserById(id);
        user.setFirstName(newFirstName);
    }

    public void deleteUser(String id) throws ElementNotFoundException {
        User user = findUserById(id);
        users.remove(user);
        
        // Supprimer aussi toutes les tâches de cet utilisateur
        tasks.removeIf(task -> task.getCreatedBy().getId().equals(id));
    }

    // === MÉTHODES POUR LES TÂCHES ===

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByUser(User user) {
        return tasks.stream()
                .filter(task -> task.getCreatedBy().getId().equals(user.getId()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<DatedTask> getAllDatedTasks() {
        return tasks.stream()
                .filter(task -> task instanceof DatedTask)
                .map(task -> (DatedTask) task)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public Task findTaskById(String id) throws ElementNotFoundException {
        Optional<Task> task = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
        
        if (task.isPresent()) {
            return task.get();
        } else {
            throw new ElementNotFoundException("Tâche avec l'ID '" + id + "' non trouvée");
        }
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void updateTask(String id, String title, String description, boolean done) throws ElementNotFoundException {
        Task task = findTaskById(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setDone(done);
    }

    public void updateDatedTask(String id, String title, String description, boolean done, LocalDate dueDate) throws ElementNotFoundException {
        Task task = findTaskById(id);
        if (task instanceof DatedTask) {
            DatedTask datedTask = (DatedTask) task;
            datedTask.setTitle(title);
            datedTask.setDescription(description);
            datedTask.setDone(done);
            datedTask.setDueDate(dueDate);
        } else {
            throw new ElementNotFoundException("La tâche avec l'ID '" + id + "' n'est pas une tâche avec échéance");
        }
    }

    public void markTaskAsDone(String id) throws ElementNotFoundException {
        Task task = findTaskById(id);
        task.setDone(true);
    }

    public void markTaskAsUndone(String id) throws ElementNotFoundException {
        Task task = findTaskById(id);
        task.setDone(false);
    }

    public void deleteTask(String id) throws ElementNotFoundException {
        Task task = findTaskById(id);
        tasks.remove(task);
    }

    public void deleteAllTasksByUser(User user) {
        tasks.removeIf(task -> task.getCreatedBy().getId().equals(user.getId()));
    }

    // === MÉTHODES DE STATISTIQUES ===

    public int getUserCount() {
        return users.size();
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public int getCompletedTaskCount() {
        return (int) tasks.stream().filter(Task::isDone).count();
    }

    public int getPendingTaskCount() {
        return (int) tasks.stream().filter(task -> !task.isDone()).count();
    }

    public List<DatedTask> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(task -> task instanceof DatedTask)
                .map(task -> (DatedTask) task)
                .filter(task -> !task.isDone() && task.getDueDate().isBefore(today))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<DatedTask> getUpcomingTasks() {
        LocalDate today = LocalDate.now();
        return tasks.stream()
                .filter(task -> task instanceof DatedTask)
                .map(task -> (DatedTask) task)
                .filter(task -> !task.isDone() && task.getDueDate().isAfter(today))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
} 