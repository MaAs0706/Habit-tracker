package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
    private Button logoutBtn;

    @FXML
    private Button prevMonthBtn;

    @FXML
    private Button nextMonthBtn;

    @FXML
    private Label monthYearLabel;

    @FXML
    private VBox habitsContainer;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label statusLabel;

    private List<String> habits;
    private int completedCount = 0;
    private LocalDate currentDate;

    @FXML
    public void initialize() {
        habits = new ArrayList<>();
        habits.add("eat");
        habits.add("eat again (...)");

        currentDate = LocalDate.now();

        refreshHabitsList();
        setupCalendar(currentDate);
        updateStatus();
    }

    private void refreshHabitsList() {
        habitsContainer.getChildren().clear();

        for (String habit : habits) {
            HBox habitItem = createHabitItem(habit);
            habitsContainer.getChildren().add(habitItem);
        }
    }

    private HBox createHabitItem(String habitName) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.getStyleClass().add("habit-item");

        Label nameLabel = new Label(habitName);
        nameLabel.getStyleClass().add("habit-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        Button markBtn = new Button("Mark Incomplete");
        markBtn.getStyleClass().add("habit-button");
        markBtn.setOnAction(e -> {
            if (markBtn.getText().equals("Mark Incomplete")) {
                markBtn.setText("Mark Complete");
                completedCount--;
            } else {
                markBtn.setText("Mark Incomplete");
                completedCount++;
            }
            updateStatus();
        });

        hbox.getChildren().addAll(nameLabel, markBtn);
        return hbox;
    }

    private void updateStatus() {
        int total = habits.size();
        statusLabel.setText("Today's Progress: " + completedCount + "/" + total + " habits completed");
    }

    private void setupCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();

        // Update month/year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthYearLabel.setText(date.format(formatter));

        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Add day headers (Mon–Sun)
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label lbl = new Label(days[i]);
            lbl.getStyleClass().add("calendar-header");
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
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
        }

        GridPane.setFillWidth(cell, true);
        GridPane.setFillHeight(cell, true);

        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + cellDate);
        });

        return cell;
    }

    @FXML
    private void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        setupCalendar(currentDate);
    }

    @FXML
    private void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        setupCalendar(currentDate);
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logout clicked");
        // TODO: Implement logout functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Logging out...");
        alert.showAndWait();
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
                habits.add(trimmed);
                refreshHabitsList();
                updateStatus();
                System.out.println("Added habit: " + trimmed);
            }
        });
    }

    @FXML
    private void handleEdit() {
        if (habits.isEmpty()) {
            showAlert("No Habits", "No habits available to edit.");
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
                    refreshHabitsList();
                    updateStatus();
                    System.out.println("Edited habit: " + habitToEdit + " to " + trimmedNewName);
                }
            });
        });
    }

    @FXML
    private void handleDelete() {
        if (habits.isEmpty()) {
            showAlert("No Habits", "No habits available to delete.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(habits.get(0), habits);
        dialog.setTitle("Delete Habit");
        dialog.setHeaderText("Select habit to delete");
        dialog.setContentText("Choose habit:");

        Optional<String> habitToDelete = dialog.showAndWait();
        habitToDelete.ifPresent(habit -> {
            habits.remove(habit);
            refreshHabitsList();
            updateStatus();
            System.out.println("Deleted habit: " + habit);
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}