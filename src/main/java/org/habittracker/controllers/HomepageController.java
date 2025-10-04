package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class HomepageController {

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private Button letsGoButton;

    @FXML
    private void initialize() {
        // Add event handlers for buttons
        loginButton.setOnAction(event -> handleLogin());
        signupButton.setOnAction(event -> handleSignUp());
        letsGoButton.setOnAction(event -> handleLetsGo());
    }

    private void handleLogin() {
        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/login.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Login - Habit Tracker");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading login page: " + e.getMessage());
        }
    }

    private void handleSignUp() {
        try {
            // Load the signup FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/signup.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) signupButton.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Sign Up - Habit Tracker");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading signup page: " + e.getMessage());
        }
    }

    private void handleLetsGo() {
        // For Let's Go button, you can either go to signup or login
        // Currently redirecting to signup, but you can change this
        try {
            // Load the signup FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/signup.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) letsGoButton.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Sign Up - Habit Tracker");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading signup page: " + e.getMessage());
        }
    }
}