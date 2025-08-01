package com.main.service;

import com.main.model.User;
import com.main.database.DatabaseAccess;
import com.main.exception.ElementNotFoundException;

import java.util.List;

public class UserService {
    private DatabaseAccess database;

    public UserService() {
        this.database = DatabaseAccess.getInstance();
    }

    // Créer un utilisateur
    public User createUser(String firstName) {
        User user = new User(firstName);
        database.addUser(user);
        return user;
    }

    // Trouver un utilisateur par son ID
    public User getUserById(String id) throws ElementNotFoundException {
        return database.findUserById(id);
    }

    // Trouver un utilisateur par son prénom
    public User getUserByFirstName(String firstName) throws ElementNotFoundException {
        return database.findUserByFirstName(firstName);
    }

    // Lister tous les utilisateurs
    public List<User> getAllUsers() {
        return database.getAllUsers();
    }

    // Modifier un utilisateur
    public void updateUser(String id, String firstName) throws ElementNotFoundException {
        database.updateUser(id, firstName);
    }

    // Supprimer un utilisateur
    public void deleteUser(String id) throws ElementNotFoundException {
        database.deleteUser(id);
    }

    // Compter le nombre d'utilisateurs
    public int getUserCount() {
        return database.getUserCount();
    }
} 