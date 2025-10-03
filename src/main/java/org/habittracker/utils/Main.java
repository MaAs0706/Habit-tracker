package org.habittracker.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Correct path to load the FXML file from the 'resources/habittracker' folder
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml")); 
        
        Parent root = fxmlLoader.load(); 
        
        // Use a good default size for the dashboard layout
        Scene scene = new Scene(root, 1100, 750); 
        
        stage.setTitle("Habit Tracker - Dashboard");

        // Set minimum size to prevent the UI from breaking if resized too small
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Database initialization is handled when HabitDAO is instantiated in the Controller.
        // We only need to launch the JavaFX application here.
        launch(args);
    }
}