package org.habittracker.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.habittracker.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load login.fxml from resources/habittracker/
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // Attach CSS from resources/habittracker/style.css
        scene.getStylesheets().add(getClass().getResource("/habittracker/login.css").toExternalForm());

        stage.setTitle("Habit Tracker - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✅ Connected to DB: " + conn.getCatalog());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch();
    }
}
