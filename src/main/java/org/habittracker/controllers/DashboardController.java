package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    private List<String> habits;
    private int completedCount = 0; // track how many completed today

    @FXML
    public void initialize() {
        habits = new ArrayList<>();
        habits.add("Exercise");
        habits.add("Read Book");
        habits.add("Meditate");

        HabitList.getItems().addAll(habits);

        // Custom cell factory for habits
        HabitList.setCellFactory(list -> new HabitCell());

        setupCalendar(LocalDate.now());
        updateStatus();
    }

    private void updateStatus() {
        int total = habits.size();
        statusLabel.setText("Today's Progress: " + completedCount + "/" + total + " habits completed");
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
        }

        // Position the first day of the month
        LocalDate firstDay = yearMonth.atDay(1);
        int startDayOfWeek = firstDay.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun

        int row = 1;
        int col = startDayOfWeek - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            StackPane dayCell = createDayCell(day, date);

            calendarGrid.add(dayCell, col, row);

            col++;
            if (col > 6) {
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
            dayText.getStyleClass().add("today-text");
        }

        GridPane.setFillWidth(cell, true);
        GridPane.setFillHeight(cell, true);

        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + cellDate);
        });

        return cell;
    }

    /**
     * Custom ListCell for habits: shows habit name + Mark Completed button
     */
    private class HabitCell extends ListCell<String> {
        private HBox hbox = new HBox(10);
        private Label habitLabel = new Label();
        private Button completeBtn = new Button("Mark Completed");
        private boolean completed = false;

        HabitCell() {
            hbox.getChildren().addAll(habitLabel, completeBtn);

            completeBtn.setOnAction(e -> {
                if (!completed) {
                    completed = true;
                    habitLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    completeBtn.setDisable(true);
                    completedCount++;
                    updateStatus();
                }
            });
        }

        @Override
        protected void updateItem(String habit, boolean empty) {
            super.updateItem(habit, empty);

            if (empty || habit == null) {
                setText(null);
                setGraphic(null);
            } else {
                habitLabel.setText(habit);
                setGraphic(hbox);
            }
        }
    }

    // ---------------- Existing Handlers ----------------

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
