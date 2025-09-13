package org.habittracker.services;

import org.habittracker.db.UserRepository;
import org.habittracker.models.User;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();

    // Register a new user
    public boolean register(String username, String password) {
        User user = new User(username, password);
        return userRepository.saveUser(user);
    }

    // Login: check if a user exists with username + password
    public boolean login(String username, String password) {
        User user = userRepository.findUser(username, password);
        return user != null;
    }
}
