package com.main;

import com.main.ui.TodoListUI;
import com.main.server.TodoServer;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Système TODO List ===");
        System.out.println("Choisissez le mode d'exécution :");
        System.out.println("1. Interface console");
        System.out.println("2. Serveur HTTP");
        System.out.print("Votre choix (1 ou 2) : ");
        
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        
        switch (choice) {
            case "1":
                startConsoleMode();
                break;
            case "2":
                startServerMode();
                break;
            default:
                System.out.println("Choix invalide. Démarrage en mode console par défaut.");
                startConsoleMode();
        }
    }
    
    private static void startConsoleMode() {
        System.out.println("Démarrage en mode console...");
        TodoListUI todoListUI = new TodoListUI();
        todoListUI.start();
    }
    
    private static void startServerMode() {
        System.out.println("Démarrage du serveur HTTP...");
        TodoServer server = new TodoServer();
        server.start();
    }
}