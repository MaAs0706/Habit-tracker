package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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

    private List<String> habits;

    @FXML
    public void initialize() {
        // Sample habits
        habits = new ArrayList<>();
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

        // TODO: Add click listener to show habits completed for this day
        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + day);
            // Later: Show a popup or list of completed habits
        });

        return cell;
    }

    @FXML
    private void handleAdd() {
        System.out.println("Add habit clicked");
        // TODO: Open add habit dialog
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit habit clicked");
        // TODO: Open edit habit dialog
    }

    @FXML
    private void handleDelete() {
        System.out.println("Delete habit clicked");
        // TODO: Remove selected habit from list and DB
    }
}
