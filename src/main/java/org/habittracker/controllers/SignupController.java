package org.habittracker.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.habittracker.services.AuthService;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;
    @FXML private BorderPane rootPane;
    @FXML private VBox signupContainer;
    @FXML private Label errorLabel ;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBackgroundColorAnimation();
        if (signupContainer != null) {
            addPulsatingEffect();
        }
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField != null ? confirmPasswordField.getText().trim() : password;

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠ Please fill all fields!");
            messageLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("⚠ Passwords do not match!");
            messageLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        boolean success = authService.register(username, password);

        if (success) {
            messageLabel.setText("✅ User registered successfully!");
            messageLabel.setStyle("-fx-text-fill: #4ecdc4;");

            // Navigate to dashboard screen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard.");
                errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
            }
        } else {
            messageLabel.setText("❌ Signup failed. Username may already exist.");
            messageLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    // Navigation Buttons
    @FXML
    private void goHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/homepage.fxml"));
            Scene homeScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading home page.");
            messageLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void goLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/login.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading login page.");
            messageLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    // Animation Methods
    private void addPulsatingEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(108, 99, 255, 0.4));
        glow.setRadius(15);
        glow.setSpread(0);
        glow.setOffsetY(6);

        signupContainer.setEffect(glow);

        Timeline pulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 15),
                        new KeyValue(glow.colorProperty(), Color.rgb(108, 99, 255, 0.4))
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(glow.radiusProperty(), 25, Interpolator.EASE_BOTH),
                        new KeyValue(glow.colorProperty(), Color.rgb(108, 99, 255, 0.7), Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(glow.radiusProperty(), 15, Interpolator.EASE_BOTH),
                        new KeyValue(glow.colorProperty(), Color.rgb(108, 99, 255, 0.4), Interpolator.EASE_BOTH)
                )
        );

        pulseTimeline.setCycleCount(Timeline.INDEFINITE);
        pulseTimeline.play();
    }

    private void addBackgroundColorAnimation() {
        if (rootPane == null) return;

        String[] gradients = {
                "-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #16213e);",
                "-fx-background-color: linear-gradient(to bottom right, #16213e, #1a1a2e);",
                "-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #0f3460);",
                "-fx-background-color: linear-gradient(to bottom right, #0f3460, #16213e);",
                "-fx-background-color: linear-gradient(to bottom right, #16213e, #1a1a2e);",
                "-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #16213e);"
        };

        final int[] currentIndex = {0};

        Timeline colorTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    currentIndex[0] = (currentIndex[0] + 1) % gradients.length;
                    rootPane.setStyle(gradients[currentIndex[0]]);
                })
        );

        colorTimeline.setCycleCount(Timeline.INDEFINITE);
        colorTimeline.play();
    }
}
