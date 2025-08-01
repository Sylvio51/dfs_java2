package com.main;

import com.main.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

public class StudentMain {
    public static void main(String[] args) {
        // Créer une dizaine d'étudiants
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice"));
        students.add(new Student("Bob"));
        students.add(new Student("Charlie"));
        students.add(new Student("Diana"));
        students.add(new Student("Eve"));
        students.add(new Student("Frank"));
        students.add(new Student("Grace"));
        students.add(new Student("Henry"));
        students.add(new Student("Iris"));
        students.add(new Student("Jack"));

        // Calculer les moyennes pour chaque étudiant avec forEach
        students.forEach(student -> {
            // Moyenne en maths
            double mathAvg = student.getMaths().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            student.setMathAverage(mathAvg);

            // Moyenne en français
            double frenchAvg = student.getFrench().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            student.setFrenchAverage(frenchAvg);

            // Moyenne en histoire
            double historyAvg = student.getHistory().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
            student.setHistoryAverage(historyAvg);

            // Moyenne globale
            double totalAvg = (mathAvg + frenchAvg + historyAvg) / 3.0;
            student.setTotalAverage(totalAvg);
        });

        // Afficher tous les étudiants
        System.out.println("=== LISTE DES ÉTUDIANTS ===");
        students.forEach(System.out::println);

        // Vérifier avec streams que personne n'a moins de 5 de moyenne
        boolean allAbove5 = students.stream()
                .allMatch(student -> student.getTotalAverage() >= 5.0);
        System.out.println("\nTous les étudiants ont au moins 5 de moyenne : " + allAbove5);

        // Vérifier qu'au moins un étudiant a plus de 10
        boolean atLeastOneAbove10 = students.stream()
                .anyMatch(student -> student.getTotalAverage() > 10.0);
        System.out.println("Au moins un étudiant a plus de 10 de moyenne : " + atLeastOneAbove10);

        // Trouver l'étudiant avec la meilleure moyenne
        Student bestStudent = students.stream()
                .max(Comparator.comparing(Student::getTotalAverage))
                .orElse(null);
        System.out.println("\nMeilleur étudiant : " + bestStudent);

        // Calculer la moyenne de la classe
        double classAverage = students.stream()
                .mapToDouble(Student::getTotalAverage)
                .average()
                .orElse(0.0);
        System.out.println("Moyenne de la classe : " + classAverage);

        // Créer une Map avec le prénom en clé et la moyenne de maths en valeur
        Map<String, Double> mathAverages = students.stream()
                .collect(Collectors.toMap(
                        Student::getName,
                        Student::getMathAverage
                ));

        System.out.println("\n=== MOYENNES DE MATHS PAR ÉTUDIANT ===");
        mathAverages.forEach((name, average) -> 
                System.out.println(name + " : " + average));

        // Statistiques supplémentaires
        System.out.println("\n=== STATISTIQUES SUPPLÉMENTAIRES ===");
        
        // Nombre d'étudiants avec plus de 15 en maths
        long excellentMaths = students.stream()
                .filter(student -> student.getMathAverage() > 15.0)
                .count();
        System.out.println("Étudiants excellents en maths (>15) : " + excellentMaths);

        // Moyenne en français de la classe
        double frenchClassAverage = students.stream()
                .mapToDouble(Student::getFrenchAverage)
                .average()
                .orElse(0.0);
        System.out.println("Moyenne de la classe en français : " + frenchClassAverage);

        // Moyenne en histoire de la classe
        double historyClassAverage = students.stream()
                .mapToDouble(Student::getHistoryAverage)
                .average()
                .orElse(0.0);
        System.out.println("Moyenne de la classe en histoire : " + historyClassAverage);
    }
} 