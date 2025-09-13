package org.habittracker.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load login.fxml from resources/habittracker/
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/habittracker/signup.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // Attach CSS from resources/habittracker/style.css
        scene.getStylesheets().add(getClass().getResource("/habittracker/style.css").toExternalForm());

        stage.setTitle("Habit Tracker - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
