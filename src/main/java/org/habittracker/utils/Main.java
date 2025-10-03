package org.habittracker.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.habittracker.db.DatabaseConnection; // Assuming this package exists

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load dashboard.fxml 
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml"));

        // FIX 1: Set a large enough starting size for the responsive layout to work
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // FIX 2: Attach the correct CSS file (dashboard.css)
        // Assuming dashboard.css is in the same folder as dashboard.fxml
        scene.getStylesheets().add(getClass().getResource("/habittracker/dashboard.css").toExternalForm());

        stage.setTitle("Habit Tracker - Dashboard"); // Title change reflects the correct FXML
        
        // Ensure the user can't squish the window too small later
        stage.setMinWidth(1100);
        stage.setMinHeight(750);
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Database connection logic is kept as is
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✅ Connected to DB: " + conn.getCatalog());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch(args); // Pass args to launch() is good practice
    }
}