package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {

    @FXML
    private Button addBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    // This method is called automatically after FXML is loaded
    @FXML
    private void initialize() {
        // Add button actions
        addBtn.setOnAction(event -> handleAddHabit());
        editBtn.setOnAction(event -> handleEditHabit());
        deleteBtn.setOnAction(event -> handleDeleteHabit());
    }

    private void handleAddHabit() {
        System.out.println("Add Habit button clicked!");
        // Later: Open a dialog/form to add a habit and save to DB
    }

    private void handleEditHabit() {
        System.out.println("Edit Habit button clicked!");
        // Later: Select a habit and allow editing
    }

    private void handleDeleteHabit() {
        System.out.println("Delete Habit button clicked!");
        // Later: Confirm and delete from DB
    }
}
