package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

        // Add day headers (Mon–Sun)
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label lbl = new Label(days[i]);
            lbl.getStyleClass().add("calendar-header");
            calendarGrid.add(lbl, i, 0);
            // row 0 = header row
        }

        // Position the first day of the month
        LocalDate firstDay = yearMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun

        int row = 1; // start from row 1 (row 0 used for headers)
        int col = startDayOfWeek - 1; // align first day under correct weekday

        for (int day = 1; day <= daysInMonth; day++) {
            StackPane dayCell = createDayCell(day, date);

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) { // wrap after Sunday
                col = 0;
                row++;
            }
        }
    }

    private StackPane createDayCell(int day, LocalDate currentMonth) {
        Text dayText = new Text(String.valueOf(day));
        StackPane cell = new StackPane(dayText);

        cell.getStyleClass().add("day-cell");

        LocalDate cellDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), day);

        // Highlight today
        if (cellDate.equals(LocalDate.now())) {
            cell.getStyleClass().add("today-cell");
            dayText.getStyleClass().add("today-text"); // 👈 NEW
        }

        GridPane.setFillWidth(cell, true);
        GridPane.setFillHeight(cell, true);

        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + cellDate);
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
    private void handleAnalytics(){
        System.out.println("Transfer to Analytics");
        //TODO: transfer the user to the analytics page
    }
}
