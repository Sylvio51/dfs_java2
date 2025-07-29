package com.main;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("C'est quoi ton nom BG ?");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("Salut " + name + ", comment ça va ?");
        scanner.close();
    }
}