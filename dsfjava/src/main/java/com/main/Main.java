package com.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("C'est quoi ton nom BG ?");
        String name = scanner.nextLine();
        System.out.println("Salut " + name + ", comment ça va ?");
        System.out.println("Écris tes tâches séparées par des virgules :");
        String input = scanner.nextLine();

        // tasks for tests, apprendre java, manger, continuer d'apprendre java, rentrer, se la tuer sur COD, manger, se la tuer sur COD parce que c'est un jeu incroyable !
        ArrayList<String> tasks = new ArrayList<>(Arrays.asList(input.split(",")));
        cleanTasks(tasks);

        boolean running = true;
        while (running) {
            System.out.println("\nVoici tes tâches :");
            showTasks(tasks);

            System.out.println("\nTu veux faire quoi maintenant ?");
            System.out.println("1 - Ajouter une tâche");
            System.out.println("2 - Supprimer une tâche");
            System.out.println("3 - Modifier une tâche");
            System.out.println("4 - Afficher les tâches");
            System.out.println("5 - Quitter");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Écris la nouvelle tâche :");
                    String newTask = scanner.nextLine();
                    tasks.add(newTask.trim());
                    System.out.println("✅ Tâche ajoutée !");
                    break;

                case "2":
                    System.out.println("Entre le numéro de la tâche à supprimer :");
                    showTasks(tasks);
                    int deleteIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (deleteIndex >= 0 && deleteIndex < tasks.size()) {
                        System.out.println("❌ Tâche supprimée : " + tasks.remove(deleteIndex));
                    } else {
                        System.out.println("Numéro invalide !");
                    }
                    break;

                case "3":
                    System.out.println("Quelle tâche veux-tu modifier ? (numéro)");
                    showTasks(tasks);
                    int modifyIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (modifyIndex >= 0 && modifyIndex < tasks.size()) {
                        System.out.println("Écris la nouvelle version :");
                        String updatedTask = scanner.nextLine();
                        tasks.set(modifyIndex, updatedTask.trim());
                        System.out.println("✏️ Tâche modifiée !");
                    } else {
                        System.out.println("Numéro invalide !");
                    }
                    break;

                case "4":
                    break;

                case "5":
                    running = false;
                    System.out.println("À bientôt " + name + " 😎");
                    break;

                default:
                    System.out.println("Choix invalide, réessaie.");
            }
        }

        scanner.close();
    }

    private static void cleanTasks(ArrayList<String> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.set(i, tasks.get(i).trim());
        }
    }

    private static void showTasks(ArrayList<String> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("🕳️ Tu n'as aucune tâche pour le moment !");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }
}