package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.habittracker.services.AuthService;

import java.io.IOException;

public class SignupController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText().trim() : password;

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("⚠ Passwords do not match!");
            return;
        }

        boolean success = authService.register(username, password);

        if (success) {
            messageLabel.setText("✅ User registered successfully!");

        } else {
            messageLabel.setText("❌ Signup failed. Username may already exist.");
        }
    }



}
