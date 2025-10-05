package org.habittracker.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.habittracker.services.AuthService;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private StackPane rootPane;
    @FXML private VBox loginContainer;

    // NEW BUTTONS
    @FXML private Button signupButton;
    @FXML private Button homeButton;

    private final AuthService authService = new AuthService();
    private final Random random = new Random();
    private final int NUM_PARTICLES = 120;
    private boolean mouseMoving = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupEnergyBackground();

        // Button Actions
        if (signupButton != null) {
            signupButton.setOnAction(e -> redirectTo("/habittracker/signup.fxml"));
        }
        if (homeButton != null) {
            homeButton.setOnAction(e -> redirectTo("/habittracker/homepage.fxml"));
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
            redirectTo("/habittracker/dashboard.fxml");
        } else {
            errorLabel.setText("Invalid username or password");
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void redirectTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading page");
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    private void setupEnergyBackground() {
        Pane particlePane = new Pane();
        particlePane.setMouseTransparent(true);
        particlePane.prefWidthProperty().bind(rootPane.widthProperty());
        particlePane.prefHeightProperty().bind(rootPane.heightProperty());
        rootPane.getChildren().add(0, particlePane);

        Circle[] particles = new Circle[NUM_PARTICLES];
        double[] angles = new double[NUM_PARTICLES];
        double[] speeds = new double[NUM_PARTICLES];

        for (int i = 0; i < NUM_PARTICLES; i++) {
            Circle p = new Circle(2 + random.nextDouble() * 4, Color.web("rgba(255,140,0,0.7)"));
            p.setCenterX(random.nextDouble() * 1500);
            p.setCenterY(random.nextDouble() * 1500);
            particlePane.getChildren().add(p);
            particles[i] = p;
            angles[i] = random.nextDouble() * 360;
            speeds[i] = 0.5 + random.nextDouble();
        }

        // Detect mouse movement only in background
        rootPane.setOnMouseMoved(e -> mouseMoving = !loginContainer.getBoundsInParent().contains(e.getX(), e.getY()));

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(30), ev -> {
            if (mouseMoving) {
                for (int i = 0; i < NUM_PARTICLES; i++) {
                    double angleRad = Math.toRadians(angles[i]);
                    particles[i].setCenterX(particles[i].getCenterX() + Math.cos(angleRad) * speeds[i]);
                    particles[i].setCenterY(particles[i].getCenterY() + Math.sin(angleRad) * speeds[i]);
                    angles[i] += 2;

                    // Wrap around screen edges
                    if (particles[i].getCenterX() < 0) particles[i].setCenterX(particlePane.getWidth());
                    if (particles[i].getCenterX() > particlePane.getWidth()) particles[i].setCenterX(0);
                    if (particles[i].getCenterY() < 0) particles[i].setCenterY(particlePane.getHeight());
                    if (particles[i].getCenterY() > particlePane.getHeight()) particles[i].setCenterY(0);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
