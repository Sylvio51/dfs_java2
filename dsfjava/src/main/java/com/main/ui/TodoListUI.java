package com.main.ui;

import com.main.model.User;
import com.main.model.Task;
import com.main.model.DatedTask;
import com.main.service.UserService;
import com.main.service.TaskService;
import com.main.exception.ElementNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TodoListUI {
    private UserService userService;
    private TaskService taskService;
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;

    public TodoListUI() {
        this.userService = new UserService();
        this.taskService = new TaskService();
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void start() {
        System.out.println("=== Système TODO List ===");
        
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    manageUsers();
                    break;
                case "2":
                    manageTasks();
                    break;
                case "3":
                    displayStatistics();
                    break;
                case "0":
                    System.out.println("Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide. Veuillez réessayer.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gérer les utilisateurs");
        System.out.println("2. Gérer les tâches");
        System.out.println("3. Afficher les statistiques");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void manageUsers() {
        while (true) {
            System.out.println("\n=== GESTION DES UTILISATEURS ===");
            System.out.println("1. Créer un utilisateur");
            System.out.println("2. Lister tous les utilisateurs");
            System.out.println("3. Modifier un utilisateur");
            System.out.println("4. Supprimer un utilisateur");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    createUser();
                    break;
                case "2":
                    listAllUsers();
                    break;
                case "3":
                    updateUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void manageTasks() {
        while (true) {
            System.out.println("\n=== GESTION DES TÂCHES ===");
            System.out.println("1. Créer une tâche simple");
            System.out.println("2. Créer une tâche avec échéance");
            System.out.println("3. Lister toutes les tâches");
            System.out.println("4. Lister les tâches d'un utilisateur");
            System.out.println("5. Modifier une tâche");
            System.out.println("6. Marquer une tâche comme terminée");
            System.out.println("7. Supprimer une tâche");
            System.out.println("8. Tâches en retard");
            System.out.println("9. Tâches à venir");
            System.out.println("0. Retour au menu principal");
            System.out.print("Votre choix : ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    createSimpleTask();
                    break;
                case "2":
                    createDatedTask();
                    break;
                case "3":
                    listAllTasks();
                    break;
                case "4":
                    listTasksByUser();
                    break;
                case "5":
                    updateTask();
                    break;
                case "6":
                    markTaskAsDone();
                    break;
                case "7":
                    deleteTask();
                    break;
                case "8":
                    showOverdueTasks();
                    break;
                case "9":
                    showUpcomingTasks();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void createUser() {
        System.out.print("Entrez le prénom de l'utilisateur : ");
        String firstName = scanner.nextLine();
        
        if (firstName.trim().isEmpty()) {
            System.out.println("Le prénom ne peut pas être vide.");
            return;
        }
        
        try {
            User user = userService.createUser(firstName);
            System.out.println("Utilisateur créé avec succès : " + user);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }
    }

    private void listAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé.");
            } else {
                System.out.println("Liste des utilisateurs :");
                for (User user : users) {
                    System.out.println("- " + user);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
    }

    private void updateUser() {
        listAllUsers();
        if (userService.getAllUsers().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de l'utilisateur à modifier : ");
        String id = scanner.nextLine();
        
        System.out.print("Entrez le nouveau prénom : ");
        String firstName = scanner.nextLine();
        
        try {
            userService.updateUser(id, firstName);
            System.out.println("Utilisateur modifié avec succès.");
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void deleteUser() {
        listAllUsers();
        if (userService.getAllUsers().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de l'utilisateur à supprimer : ");
        String id = scanner.nextLine();
        
        try {
            userService.deleteUser(id);
            System.out.println("Utilisateur supprimé avec succès.");
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void createSimpleTask() {
        listAllUsers();
        if (userService.getAllUsers().isEmpty()) {
            System.out.println("Aucun utilisateur disponible. Créez d'abord un utilisateur.");
            return;
        }
        
        System.out.print("Entrez l'ID de l'utilisateur : ");
        String userId = scanner.nextLine();
        
        try {
            User user = userService.getUserById(userId);
            
            System.out.print("Entrez le titre de la tâche : ");
            String title = scanner.nextLine();
            
            System.out.print("Entrez la description de la tâche : ");
            String description = scanner.nextLine();
            
            Task task = taskService.createTask(title, description, user);
            System.out.println("Tâche créée avec succès : " + task);
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la tâche : " + e.getMessage());
        }
    }

    private void createDatedTask() {
        listAllUsers();
        if (userService.getAllUsers().isEmpty()) {
            System.out.println("Aucun utilisateur disponible. Créez d'abord un utilisateur.");
            return;
        }
        
        System.out.print("Entrez l'ID de l'utilisateur : ");
        String userId = scanner.nextLine();
        
        try {
            User user = userService.getUserById(userId);
            
            System.out.print("Entrez le titre de la tâche : ");
            String title = scanner.nextLine();
            
            System.out.print("Entrez la description de la tâche : ");
            String description = scanner.nextLine();
            
            System.out.print("Entrez la date d'échéance (format dd/MM/yyyy) : ");
            String dateStr = scanner.nextLine();
            
            try {
                LocalDate dueDate = LocalDate.parse(dateStr, dateFormatter);
                DatedTask task = taskService.createDatedTask(title, description, user, dueDate);
                System.out.println("Tâche avec échéance créée avec succès : " + task);
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide. Utilisez le format dd/MM/yyyy.");
            }
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la tâche : " + e.getMessage());
        }
    }

    private void listAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            if (tasks.isEmpty()) {
                System.out.println("Aucune tâche trouvée.");
            } else {
                System.out.println("Liste de toutes les tâches :");
                for (Task task : tasks) {
                    System.out.println("- " + task);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des tâches : " + e.getMessage());
        }
    }

    private void listTasksByUser() {
        listAllUsers();
        if (userService.getAllUsers().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de l'utilisateur : ");
        String userId = scanner.nextLine();
        
        try {
            User user = userService.getUserById(userId);
            List<Task> tasks = taskService.getTasksByUser(user);
            if (tasks.isEmpty()) {
                System.out.println("Aucune tâche trouvée pour cet utilisateur.");
            } else {
                System.out.println("Tâches de " + user.getFirstName() + " :");
                for (Task task : tasks) {
                    System.out.println("- " + task);
                }
            }
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des tâches : " + e.getMessage());
        }
    }

    private void updateTask() {
        listAllTasks();
        if (taskService.getAllTasks().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de la tâche à modifier : ");
        String taskId = scanner.nextLine();
        
        System.out.print("Entrez le nouveau titre : ");
        String title = scanner.nextLine();
        
        System.out.print("Entrez la nouvelle description : ");
        String description = scanner.nextLine();
        
        System.out.print("La tâche est-elle terminée ? (oui/non) : ");
        String doneStr = scanner.nextLine();
        boolean done = doneStr.toLowerCase().startsWith("o");
        
        try {
            taskService.updateTask(taskId, title, description, done);
            System.out.println("Tâche modifiée avec succès.");
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    private void markTaskAsDone() {
        listAllTasks();
        if (taskService.getAllTasks().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de la tâche à marquer comme terminée : ");
        String taskId = scanner.nextLine();
        
        try {
            taskService.markTaskAsDone(taskId);
            System.out.println("Tâche marquée comme terminée.");
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors du marquage : " + e.getMessage());
        }
    }

    private void deleteTask() {
        listAllTasks();
        if (taskService.getAllTasks().isEmpty()) {
            return;
        }
        
        System.out.print("Entrez l'ID de la tâche à supprimer : ");
        String taskId = scanner.nextLine();
        
        try {
            taskService.deleteTask(taskId);
            System.out.println("Tâche supprimée avec succès.");
        } catch (ElementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void showOverdueTasks() {
        try {
            List<DatedTask> overdueTasks = taskService.getOverdueTasks();
            if (overdueTasks.isEmpty()) {
                System.out.println("Aucune tâche en retard.");
            } else {
                System.out.println("Tâches en retard :");
                for (DatedTask task : overdueTasks) {
                    System.out.println("- " + task);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des tâches en retard : " + e.getMessage());
        }
    }

    private void showUpcomingTasks() {
        try {
            List<DatedTask> upcomingTasks = taskService.getUpcomingTasks();
            if (upcomingTasks.isEmpty()) {
                System.out.println("Aucune tâche à venir.");
            } else {
                System.out.println("Tâches à venir :");
                for (DatedTask task : upcomingTasks) {
                    System.out.println("- " + task);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des tâches à venir : " + e.getMessage());
        }
    }

    private void displayStatistics() {
        try {
            System.out.println("\n=== STATISTIQUES ===");
            System.out.println("Nombre d'utilisateurs : " + userService.getUserCount());
            System.out.println("Nombre total de tâches : " + taskService.getTotalTaskCount());
            System.out.println("Tâches terminées : " + taskService.getCompletedTaskCount());
            System.out.println("Tâches en attente : " + taskService.getPendingTaskCount());
            System.out.println("Tâches en retard : " + taskService.getOverdueTasks().size());
            System.out.println("Tâches à venir : " + taskService.getUpcomingTasks().size());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'affichage des statistiques : " + e.getMessage());
        }
    }
} 