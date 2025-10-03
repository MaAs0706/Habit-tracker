package org.habittracker.services;

import org.habittracker.db.UserRepository;
import org.habittracker.models.User;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    // Register a new user
    public boolean register(String username, String password) {
        // Check if username already exists
        if (userRepository.findUserByUsername(username) != null) {
            return false; // Username taken
        }

        User user = new User(username, password); // store plain text
        return userRepository.saveUser(user);
    }

    // Login: check if a user exists with username + password
    public boolean login(String username, String password) {
        User user = userRepository.findUserByUsername(username);
        return user != null && user.getPassword().equals(password); // compare plain text
    }

    // Optional: fetch logged-in user
    public User getUser(String username) {
        return userRepository.findUserByUsername(username);
    }
}