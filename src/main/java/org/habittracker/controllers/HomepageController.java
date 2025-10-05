package org.habittracker.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class HomepageController {

    @FXML
    private StackPane root; // Must match FXML root type

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private Button letsGoButton;

    private final int NUM_SNOWFLAKES = 100;
    private final Random random = new Random();

    @FXML
    private void initialize() {
        // Button handlers
        loginButton.setOnAction(event -> handleLogin());
        signupButton.setOnAction(event -> handleSignUp());
        letsGoButton.setOnAction(event -> handleLetsGo());

        // --- Snow Animation ---
        Pane snowPane = new Pane();
        snowPane.setMouseTransparent(true); // clicks pass through
        snowPane.prefWidthProperty().bind(root.widthProperty());
        snowPane.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(0, snowPane); // add behind all content

        Circle[] snowflakes = new Circle[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; i++) {
            Circle flake = new Circle(2 + random.nextDouble() * 3, Color.WHITE);
            flake.setCenterX(random.nextDouble() * 1200); // initial X
            flake.setCenterY(random.nextDouble() * 800);  // initial Y
            snowPane.getChildren().add(flake);
            snowflakes[i] = flake;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(30), e -> {
            for (Circle flake : snowflakes) {
                flake.setCenterY(flake.getCenterY() + 1 + random.nextDouble() * 2);
                flake.setCenterX(flake.getCenterX() + Math.sin(flake.getCenterY() / 50) * 0.5);
                if (flake.getCenterY() > snowPane.getHeight()) {
                    flake.setCenterY(0);
                    flake.setCenterX(random.nextDouble() * snowPane.getWidth());
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Login - Habit Tracker");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading login page: " + e.getMessage());
        }
    }

    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Sign Up - Habit Tracker");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading signup page: " + e.getMessage());
        }
    }

    private void handleLetsGo() {
        // Redirect to signup page
        handleSignUp();
    }
}