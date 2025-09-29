package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML
    private Button addBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private ListView<String> HabitList;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label statusLabel;

    private List<String> habits = new ArrayList<>();

    @FXML
    public void initialize() {
        // Load habits here, for now sample data
        habits.add("Exercise");
        habits.add("Read Book");
        habits.add("Meditate");

        HabitList.getItems().addAll(habits);

        setupCalendar(LocalDate.now());
        updateStatus();
    }

    private void updateStatus() {
        // Example: 2 out of 3 habits done
        int completed = 2;
        int total = habits.size();
        statusLabel.setText("Today's Progress: " + completed + "/" + total + " habits completed");
    }

    private void setupCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();

        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 0;
        int col = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            StackPane dayCell = createDayCell(day);
            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) { // 7 days a week
                col = 0;
                row++;
            }
        }
    }

    private StackPane createDayCell(int day) {
        Rectangle rect = new Rectangle(80, 60);
        rect.setFill(Color.LIGHTGRAY);
        rect.setStroke(Color.BLACK);

        Text dayText = new Text(String.valueOf(day));

        StackPane cell = new StackPane();
        cell.getChildren().addAll(rect, dayText);

        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + day);
            // TODO: Show habits completed for this day
        });

        return cell;
    }

    @FXML
    private void handleAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Habit");
        dialog.setHeaderText("Add a new habit");
        dialog.setContentText("Enter habit name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(habit -> {
            String trimmed = habit.trim();
            if (!trimmed.isEmpty() && !habits.contains(trimmed)) {
                // TODO: Add habit to DB here

                habits.add(trimmed);
                HabitList.getItems().add(trimmed);
                updateStatus();
                System.out.println("Added habit: " + trimmed);
            }
        });
    }

    @FXML
    private void handleEdit() {
        if (habits.isEmpty()) {
            System.out.println("No habits to edit");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(habits.get(0), habits);
        dialog.setTitle("Edit Habit");
        dialog.setHeaderText("Select habit to edit");
        dialog.setContentText("Choose habit:");

        Optional<String> selectedHabit = dialog.showAndWait();

        selectedHabit.ifPresent(habitToEdit -> {
            TextInputDialog editDialog = new TextInputDialog(habitToEdit);
            editDialog.setTitle("Edit Habit");
            editDialog.setHeaderText("Editing habit: " + habitToEdit);
            editDialog.setContentText("Enter new habit name:");

            Optional<String> newName = editDialog.showAndWait();
            newName.ifPresent(newHabitName -> {
                String trimmedNewName = newHabitName.trim();
                if (!trimmedNewName.isEmpty() && !habits.contains(trimmedNewName)) {
                    // TODO: Update habit in DB here

                    int index = habits.indexOf(habitToEdit);
                    habits.set(index, trimmedNewName);
                    HabitList.getItems().set(index, trimmedNewName);
                    updateStatus();
                    System.out.println("Edited habit: " + habitToEdit + " to " + trimmedNewName);
                }
            });
        });
    }

    @FXML
    private void handleDelete() {
        if (habits.isEmpty()) {
            System.out.println("No habits to delete");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(habits.get(0), habits);
        dialog.setTitle("Delete Habit");
        dialog.setHeaderText("Select habit to delete");
        dialog.setContentText("Choose habit:");

        Optional<String> habitToDelete = dialog.showAndWait();
        habitToDelete.ifPresent(habit -> {
            // TODO: Delete habit in DB here

            habits.remove(habit);
            HabitList.getItems().remove(habit);
            updateStatus();
            System.out.println("Deleted habit: " + habit);
        });
    }

    @FXML
    private void handleAnalytics() {
        System.out.println("Transfer to Analytics");
        // TODO: Implement analytics page transfer
    }

}