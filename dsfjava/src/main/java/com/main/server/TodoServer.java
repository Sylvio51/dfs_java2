package com.main.server;

import com.main.database.DatabaseAccess;
import com.main.model.User;
import com.main.model.Task;
import com.main.model.DatedTask;
import com.main.exception.ElementNotFoundException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TodoServer {
    private static final int PORT = 8080;
    private DatabaseAccess database;
    private DateTimeFormatter dateFormatter;

    public TodoServer() {
        this.database = DatabaseAccess.getInstance();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur TODO List démarré sur le port " + PORT);
            System.out.println("Accédez à http://localhost:" + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            // Lire la première ligne de la requête HTTP
            String requestLine = in.readLine();
            if (requestLine == null) {
                sendErrorResponse(out, "Requête invalide");
                return;
            }

            // Parser la requête
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                sendErrorResponse(out, "Format de requête invalide");
                return;
            }

            String method = parts[0];
            String path = parts[1];

            // Lire les headers HTTP et le body pour POST
            String line;
            StringBuilder body = new StringBuilder();
            boolean readingBody = false;
            int contentLength = 0;
            
            while ((line = in.readLine()) != null) {
                if (line.isEmpty() && !readingBody) {
                    readingBody = true;
                    continue;
                }
                if (readingBody) {
                    body.append(line);
                } else if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(16).trim());
                }
            }

            // Traiter la requête
            handleRequest(method, path, body.toString(), out);

        } catch (IOException e) {
            System.err.println("Erreur lors du traitement de la requête : " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    private void handleRequest(String method, String path, String body, PrintWriter out) {
        try {
            if (method.equals("GET")) {
                handleGetRequest(path, out);
            } else if (method.equals("POST")) {
                handlePostRequest(path, body, out);
            } else {
                sendErrorResponse(out, "Méthode non supportée : " + method);
            }
        } catch (Exception e) {
            sendErrorResponse(out, "Erreur interne : " + e.getMessage());
        }
    }

    private void handleGetRequest(String path, PrintWriter out) {
        if (path.equals("/") || path.equals("/index")) {
            sendHtmlResponse(out, generateMainPage());
        } else if (path.equals("/users")) {
            sendHtmlResponse(out, generateUsersPage());
        } else if (path.equals("/tasks")) {
            sendHtmlResponse(out, generateTasksPage());
        } else if (path.equals("/stats")) {
            sendHtmlResponse(out, generateStatsPage());
        } else if (path.startsWith("/user/")) {
            String userId = path.substring(6);
            sendHtmlResponse(out, generateUserTasksPage(userId));
        } else if (path.equals("/create-user-form")) {
            sendHtmlResponse(out, generateCreateUserForm());
        } else if (path.equals("/create-task-form")) {
            sendHtmlResponse(out, generateCreateTaskForm());
        } else {
            sendErrorResponse(out, "Page non trouvée : " + path);
        }
    }

    private void handlePostRequest(String path, String body, PrintWriter out) {
        if (path.equals("/create-user")) {
            handleCreateUser(body, out);
        } else if (path.equals("/create-task")) {
            handleCreateTask(body, out);
        } else if (path.equals("/delete-user")) {
            handleDeleteUser(body, out);
        } else if (path.equals("/delete-task")) {
            handleDeleteTask(body, out);
        } else {
            sendErrorResponse(out, "Action non supportée : " + path);
        }
    }

    private void handleCreateUser(String body, PrintWriter out) {
        String firstName = parseFormData(body, "firstName");
        if (firstName != null && !firstName.trim().isEmpty()) {
            User user = new User(firstName.trim());
            database.addUser(user);
            sendHtmlResponse(out, generateSuccessPage("Utilisateur créé avec succès", user.toString(), "/users"));
        } else {
            sendErrorResponse(out, "Le prénom est obligatoire");
        }
    }

    private void handleCreateTask(String body, PrintWriter out) {
        String title = parseFormData(body, "title");
        String description = parseFormData(body, "description");
        String userId = parseFormData(body, "userId");
        String dueDate = parseFormData(body, "dueDate");
        
        if (title != null && !title.trim().isEmpty() && userId != null) {
            try {
                User user = database.findUserById(userId);
                Task task;
                
                if (dueDate != null && !dueDate.trim().isEmpty()) {
                    LocalDate date = LocalDate.parse(dueDate.trim(), dateFormatter);
                    task = new DatedTask(title.trim(), description.trim(), user, date);
                } else {
                    task = new Task(title.trim(), description.trim(), user);
                }
                
                database.addTask(task);
                sendHtmlResponse(out, generateSuccessPage("Tâche créée avec succès", task.toString(), "/tasks"));
            } catch (ElementNotFoundException e) {
                sendErrorResponse(out, "Utilisateur non trouvé");
            } catch (Exception e) {
                sendErrorResponse(out, "Erreur lors de la création : " + e.getMessage());
            }
        } else {
            sendErrorResponse(out, "Le titre et l'utilisateur sont obligatoires");
        }
    }

    private void handleDeleteUser(String body, PrintWriter out) {
        String userId = parseFormData(body, "userId");
        if (userId != null) {
            try {
                database.deleteUser(userId);
                sendHtmlResponse(out, generateSuccessPage("Utilisateur supprimé avec succès", "", "/users"));
            } catch (ElementNotFoundException e) {
                sendErrorResponse(out, "Utilisateur non trouvé");
            }
        } else {
            sendErrorResponse(out, "ID utilisateur manquant");
        }
    }

    private void handleDeleteTask(String body, PrintWriter out) {
        String taskId = parseFormData(body, "taskId");
        if (taskId != null) {
            try {
                database.deleteTask(taskId);
                sendHtmlResponse(out, generateSuccessPage("Tâche supprimée avec succès", "", "/tasks"));
            } catch (ElementNotFoundException e) {
                sendErrorResponse(out, "Tâche non trouvée");
            }
        } else {
            sendErrorResponse(out, "ID tâche manquant");
        }
    }

    private String parseFormData(String body, String fieldName) {
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(fieldName)) {
                try {
                    return java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                } catch (Exception e) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    private void sendHtmlResponse(PrintWriter out, String html) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=UTF-8");
        try {
            out.println("Content-Length: " + html.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            out.println("Content-Length: " + html.length());
        }
        out.println();
        out.println(html);
    }

    private void sendErrorResponse(PrintWriter out, String message) {
        String html = generateErrorPage(message);
        out.println("HTTP/1.1 400 Bad Request");
        out.println("Content-Type: text/html; charset=UTF-8");
        try {
            out.println("Content-Length: " + html.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            out.println("Content-Length: " + html.length());
        }
        out.println();
        out.println(html);
    }

    private String generateMainPage() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>TODO List - Accueil</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; text-align: center; }");
        html.append("nav { margin: 20px 0; }");
        html.append("nav ul { list-style: none; padding: 0; display: flex; justify-content: center; gap: 20px; }");
        html.append("nav a { text-decoration: none; color: #007bff; padding: 10px 20px; border: 1px solid #007bff; border-radius: 5px; }");
        html.append("nav a:hover { background-color: #007bff; color: white; }");
        html.append(".actions { margin: 20px 0; text-align: center; }");
        html.append(".btn { display: inline-block; padding: 10px 20px; margin: 5px; text-decoration: none; color: white; border-radius: 5px; }");
        html.append(".btn-primary { background-color: #007bff; }");
        html.append(".btn-success { background-color: #28a745; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>📋 Système TODO List</h1>");
        html.append("<nav><ul>");
        html.append("<li><a href='/users'>👥 Gérer les utilisateurs</a></li>");
        html.append("<li><a href='/tasks'>📝 Gérer les tâches</a></li>");
        html.append("<li><a href='/stats'>📊 Statistiques</a></li>");
        html.append("</ul></nav>");
        html.append("<div class='actions'>");
        html.append("<a href='/create-user-form' class='btn btn-primary'>➕ Créer un utilisateur</a>");
        html.append("<a href='/create-task-form' class='btn btn-success'>➕ Créer une tâche</a>");
        html.append("</div>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateUsersPage() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Utilisateurs</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".back-link { margin-bottom: 20px; }");
        html.append(".back-link a { color: #007bff; text-decoration: none; }");
        html.append(".user-list { list-style: none; padding: 0; }");
        html.append(".user-item { background: #f8f9fa; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #007bff; }");
        html.append(".user-name { font-weight: bold; color: #333; }");
        html.append(".user-id { color: #666; font-size: 0.9em; }");
        html.append(".delete-form { display: inline; }");
        html.append(".delete-btn { background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>👥 Liste des utilisateurs</h1>");
        html.append("<div class='back-link'><a href='/'>← Retour à l'accueil</a></div>");
        
        List<User> users = database.getAllUsers();
        if (users.isEmpty()) {
            html.append("<p>Aucun utilisateur trouvé.</p>");
        } else {
            html.append("<ul class='user-list'>");
            for (User user : users) {
                html.append("<li class='user-item'>");
                html.append("<div class='user-name'>").append(user.getFirstName()).append("</div>");
                html.append("<div class='user-id'>ID: ").append(user.getId()).append("</div>");
                html.append("<form method='POST' action='/delete-user' class='delete-form'>");
                html.append("<input type='hidden' name='userId' value='").append(user.getId()).append("'>");
                html.append("<button type='submit' class='delete-btn'>🗑️ Supprimer</button>");
                html.append("</form>");
                html.append("</li>");
            }
            html.append("</ul>");
        }
        
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateTasksPage() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Tâches</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".back-link { margin-bottom: 20px; }");
        html.append(".back-link a { color: #007bff; text-decoration: none; }");
        html.append(".task-list { list-style: none; padding: 0; }");
        html.append(".task-item { background: #f8f9fa; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #28a745; }");
        html.append(".task-title { font-weight: bold; color: #333; font-size: 1.1em; }");
        html.append(".task-desc { color: #666; margin: 5px 0; }");
        html.append(".task-meta { color: #888; font-size: 0.9em; }");
        html.append(".task-done { border-left-color: #28a745; }");
        html.append(".task-pending { border-left-color: #ffc107; }");
        html.append(".delete-form { display: inline; }");
        html.append(".delete-btn { background: #dc3545; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>📝 Liste des tâches</h1>");
        html.append("<div class='back-link'><a href='/'>← Retour à l'accueil</a></div>");
        
        List<Task> tasks = database.getAllTasks();
        if (tasks.isEmpty()) {
            html.append("<p>Aucune tâche trouvée.</p>");
        } else {
            html.append("<ul class='task-list'>");
            for (Task task : tasks) {
                String statusClass = task.isDone() ? "task-done" : "task-pending";
                String statusText = task.isDone() ? "✅ Terminée" : "⏳ En cours";
                
                html.append("<li class='task-item ").append(statusClass).append("'>");
                html.append("<div class='task-title'>").append(task.getTitle()).append("</div>");
                html.append("<div class='task-desc'>").append(task.getDescription()).append("</div>");
                html.append("<div class='task-meta'>");
                html.append("Créée par: ").append(task.getCreatedBy().getFirstName());
                html.append(" | ID: ").append(task.getId());
                if (task instanceof DatedTask) {
                    html.append(" | Échéance: ").append(((DatedTask) task).getDueDate());
                }
                html.append(" | ").append(statusText);
                html.append("</div>");
                html.append("<form method='POST' action='/delete-task' class='delete-form'>");
                html.append("<input type='hidden' name='taskId' value='").append(task.getId()).append("'>");
                html.append("<button type='submit' class='delete-btn'>🗑️ Supprimer</button>");
                html.append("</form>");
                html.append("</li>");
            }
            html.append("</ul>");
        }
        
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateCreateUserForm() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Créer un utilisateur</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".form-group { margin: 15px 0; }");
        html.append("label { display: block; margin-bottom: 5px; font-weight: bold; }");
        html.append("input[type='text'] { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 16px; }");
        html.append(".btn { padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }");
        html.append(".btn-primary { background-color: #007bff; color: white; }");
        html.append(".btn-secondary { background-color: #6c757d; color: white; text-decoration: none; display: inline-block; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>👤 Créer un utilisateur</h1>");
        html.append("<form method='POST' action='/create-user'>");
        html.append("<div class='form-group'>");
        html.append("<label for='firstName'>Prénom :</label>");
        html.append("<input type='text' id='firstName' name='firstName' required>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<button type='submit' class='btn btn-primary'>Créer l'utilisateur</button>");
        html.append("<a href='/users' class='btn btn-secondary'>Annuler</a>");
        html.append("</div>");
        html.append("</form>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateCreateTaskForm() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Créer une tâche</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".form-group { margin: 15px 0; }");
        html.append("label { display: block; margin-bottom: 5px; font-weight: bold; }");
        html.append("input[type='text'], textarea, select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 16px; }");
        html.append("textarea { height: 100px; resize: vertical; }");
        html.append(".btn { padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }");
        html.append(".btn-primary { background-color: #007bff; color: white; }");
        html.append(".btn-secondary { background-color: #6c757d; color: white; text-decoration: none; display: inline-block; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>📝 Créer une tâche</h1>");
        html.append("<form method='POST' action='/create-task'>");
        html.append("<div class='form-group'>");
        html.append("<label for='title'>Titre :</label>");
        html.append("<input type='text' id='title' name='title' required>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<label for='description'>Description :</label>");
        html.append("<textarea id='description' name='description'></textarea>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<label for='userId'>Utilisateur :</label>");
        html.append("<select id='userId' name='userId' required>");
        html.append("<option value=''>Choisir un utilisateur</option>");
        
        List<User> users = database.getAllUsers();
        for (User user : users) {
            html.append("<option value='").append(user.getId()).append("'>");
            html.append(user.getFirstName()).append("</option>");
        }
        
        html.append("</select>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<label for='dueDate'>Date d'échéance (optionnel) :</label>");
        html.append("<input type='text' id='dueDate' name='dueDate' placeholder='dd/MM/yyyy'>");
        html.append("</div>");
        html.append("<div class='form-group'>");
        html.append("<button type='submit' class='btn btn-primary'>Créer la tâche</button>");
        html.append("<a href='/tasks' class='btn btn-secondary'>Annuler</a>");
        html.append("</div>");
        html.append("</form>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateSuccessPage(String title, String details, String backUrl) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Succès</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }");
        html.append("h1 { color: #28a745; }");
        html.append(".details { background: #f8f9fa; padding: 15px; margin: 20px 0; border-radius: 5px; text-align: left; }");
        html.append(".btn { padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; text-decoration: none; display: inline-block; }");
        html.append(".btn-primary { background-color: #007bff; color: white; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>✅ ").append(title).append("</h1>");
        if (!details.isEmpty()) {
            html.append("<div class='details'>").append(details).append("</div>");
        }
        html.append("<a href='").append(backUrl).append("' class='btn btn-primary'>Retour</a>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateErrorPage(String message) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Erreur</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }");
        html.append("h1 { color: #dc3545; }");
        html.append(".error-message { background: #f8d7da; color: #721c24; padding: 15px; margin: 20px 0; border-radius: 5px; }");
        html.append(".btn { padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; text-decoration: none; display: inline-block; }");
        html.append(".btn-primary { background-color: #007bff; color: white; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>❌ Erreur</h1>");
        html.append("<div class='error-message'>").append(message).append("</div>");
        html.append("<a href='/' class='btn btn-primary'>Retour à l'accueil</a>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateUserTasksPage(String userId) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Tâches de l'utilisateur</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".back-link { margin-bottom: 20px; }");
        html.append(".back-link a { color: #007bff; text-decoration: none; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>Tâches de l'utilisateur ").append(userId).append("</h1>");
        html.append("<div class='back-link'><a href='/users'>← Retour aux utilisateurs</a></div>");
        
        try {
            User user = database.findUserById(userId);
            List<Task> tasks = database.getTasksByUser(user);
            if (tasks.isEmpty()) {
                html.append("<p>Aucune tâche trouvée pour cet utilisateur.</p>");
            } else {
                html.append("<ul>");
                for (Task task : tasks) {
                    html.append("<li>").append(task.toString()).append("</li>");
                }
                html.append("</ul>");
            }
        } catch (ElementNotFoundException e) {
            html.append("<p>Utilisateur non trouvé : ").append(e.getMessage()).append("</p>");
        }
        
        html.append("</div></body></html>");
        return html.toString();
    }

    private String generateStatsPage() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Statistiques</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f5f5; }");
        html.append(".container { max-width: 800px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append("h1 { color: #333; }");
        html.append(".back-link { margin-bottom: 20px; }");
        html.append(".back-link a { color: #007bff; text-decoration: none; }");
        html.append(".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-top: 20px; }");
        html.append(".stat-card { background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; }");
        html.append(".stat-number { font-size: 2em; font-weight: bold; color: #007bff; }");
        html.append(".stat-label { color: #666; margin-top: 5px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");
        html.append("<h1>📊 Statistiques</h1>");
        html.append("<div class='back-link'><a href='/'>← Retour à l'accueil</a></div>");
        
        html.append("<div class='stats-grid'>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getUserCount()).append("</div>");
        html.append("<div class='stat-label'>Utilisateurs</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getTaskCount()).append("</div>");
        html.append("<div class='stat-label'>Tâches totales</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getCompletedTaskCount()).append("</div>");
        html.append("<div class='stat-label'>Tâches terminées</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getPendingTaskCount()).append("</div>");
        html.append("<div class='stat-label'>Tâches en attente</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getOverdueTasks().size()).append("</div>");
        html.append("<div class='stat-label'>Tâches en retard</div>");
        html.append("</div>");
        html.append("<div class='stat-card'>");
        html.append("<div class='stat-number'>").append(database.getUpcomingTasks().size()).append("</div>");
        html.append("<div class='stat-label'>Tâches à venir</div>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div></body></html>");
        return html.toString();
    }
} 