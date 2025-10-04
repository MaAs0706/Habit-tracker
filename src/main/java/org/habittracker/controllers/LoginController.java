package org.habittracker.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.habittracker.services.AuthService;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;
    
    @FXML
    private BorderPane rootPane;
    
    @FXML
    private VBox loginContainer;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Start background animation
        addBackgroundColorAnimation();
        
        // Start pulsating glow effect on login container
        if (loginContainer != null) {
            addPulsatingEffect();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        boolean success = authService.login(username, password);

        if (success) {
            errorLabel.setText("Login successful!");
            errorLabel.setStyle("-fx-text-fill: #4ecdc4;");
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
            errorLabel.setText("Invalid username or password");
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }
    
    // Animation Methods
    private void addPulsatingEffect() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(108, 99, 255, 0.4));
        glow.setRadius(15);
        glow.setSpread(0);
        glow.setOffsetY(6);
        
        loginContainer.setEffect(glow);
        
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