package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.habittracker.services.AuthService;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();


        boolean success = authService.login(username,password);


        if (success) {
            errorLabel.setText("Login successful!");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard.");
            }
        } else {
            errorLabel.setText("Invalid username or password");
        }
    }
}