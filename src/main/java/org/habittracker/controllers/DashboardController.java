package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import javafx.stage.Stage;
import org.habittracker.db.HabitDAO;
import org.habittracker.models.Habit;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardController {

    @FXML
    private Label monthYearLabel;
    @FXML
    private VBox habitsContainer;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField newHabitField;

    private List<Habit> habits;
    private final HabitDAO habitDAO = new HabitDAO();
    private Map<LocalDate, Map<Integer, Boolean>> dailyHabitStatus;

    private LocalDate currentDate;
    private LocalDate selectedDate;

    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    @FXML
    public void initialize() {
        dailyHabitStatus = new HashMap<>();
        currentDate = LocalDate.now();
        selectedDate = currentDate;

        loadHabits();
        loadMonthCompletionStatus(currentDate);

        setupCalendar(currentDate);
        selectDay(currentDate);
    }

    private void loadHabits() {
        habits = habitDAO.getAllHabits();
        if (habits.isEmpty()) {
            habitDAO.addHabit(new Habit("Read 20 pages"));
            habitDAO.addHabit(new Habit("Drink 2L water"));
            habits = habitDAO.getAllHabits();
        }
    }

    private void loadMonthCompletionStatus(LocalDate date) {
        dailyHabitStatus.clear();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate dayDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            Map<Integer, Boolean> dayStatus = habitDAO.getCompletionStatusForDay(dayDate);
            if (!dayStatus.isEmpty())
                dailyHabitStatus.put(dayDate, dayStatus);
        }
    }

    private void setupCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear();

        YearMonth yearMonth = YearMonth.from(date);
        monthYearLabel.setText(yearMonth.format(MONTH_YEAR_FORMATTER));

        LocalDate calendarStart = yearMonth.atDay(1);
        int dayOfWeek = calendarStart.getDayOfWeek().getValue();
        int offset = dayOfWeek == 7 ? 6 : dayOfWeek - 1;
        LocalDate firstDayOfGrid = calendarStart.minusDays(offset);

        for (int row = 0; row < 6; row++) {
            RowConstraints rowC = new RowConstraints();
            rowC.setPercentHeight(100.0 / 6);
            calendarGrid.getRowConstraints().add(rowC);

            for (int col = 0; col < 7; col++) {
                LocalDate dayDate = firstDayOfGrid.plusDays(row * 7L + col);
                StackPane dayCell = createDayCell(dayDate, yearMonth);
                calendarGrid.add(dayCell, col, row);
                updateCellColor(dayDate, dayCell);
            }
        }
    }

    private StackPane createDayCell(LocalDate dayDate, YearMonth currentMonth) {
        StackPane cell = new StackPane();
        Text text = new Text(String.valueOf(dayDate.getDayOfMonth()));
        cell.getChildren().add(text);

        if (!YearMonth.from(dayDate).equals(currentMonth)) {
            cell.getStyleClass().add("other-month");
        } else {
            cell.setOnMouseClicked(e -> selectDay(dayDate));
        }

        StackPane.setAlignment(text, Pos.TOP_LEFT);
        cell.getStyleClass().add("day-cell");
        return cell;
    }

    private void updateCellColor(LocalDate date, StackPane cell) {
        cell.getStyleClass().removeAll("day-cell-complete", "day-cell-partial", "day-cell-missed", "today-cell",
                "selected-cell");
        if (!cell.getStyleClass().contains("day-cell"))
            cell.getStyleClass().add("day-cell");

        long totalHabits = habits.size();
        if (totalHabits > 0) {
            Map<Integer, Boolean> status = dailyHabitStatus.getOrDefault(date, new HashMap<>());
            long completedCount = status.values().stream().filter(b -> b).count();

            if (completedCount == totalHabits)
                cell.getStyleClass().add("day-cell-complete");
            else if (completedCount > 0)
                cell.getStyleClass().add("day-cell-partial");
            else if (date.isBefore(LocalDate.now()))
                cell.getStyleClass().add("day-cell-missed");
        }

        if (date.equals(LocalDate.now()))
            cell.getStyleClass().add("today-cell");
        if (date.equals(selectedDate) && YearMonth.from(date).equals(YearMonth.from(currentDate)))
            cell.getStyleClass().add("selected-cell");
    }

    private void refreshCalendarView() {
        loadMonthCompletionStatus(currentDate);
        setupCalendar(currentDate);
        refreshHabitList(selectedDate);
    }

    private void selectDay(LocalDate date) {
        selectedDate = date;
        refreshHabitList(date);
        refreshCalendarView();
    }

    private void refreshHabitList(LocalDate date) {
        habitsContainer.getChildren().clear();
        Map<Integer, Boolean> dayStatus = habitDAO.getCompletionStatusForDay(date);

        for (Habit habit : habits) {
            boolean completed = dayStatus.getOrDefault(habit.getId(), false);
            HBox habitItem = createHabitItem(habit, completed);
            habitsContainer.getChildren().add(habitItem);
        }
        updateStatusLabel();
    }

    private HBox createHabitItem(Habit habit, boolean completed) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("habit-item");

        CheckBox checkBox = new CheckBox(habit.getName());
        checkBox.setSelected(completed);

        if (completed)
            item.getStyleClass().add("habit-item-completed");

        item.getChildren().add(checkBox);
        checkBox.setOnAction(e -> {
            habitDAO.markCompleted(habit, selectedDate, checkBox.isSelected());
            if (checkBox.isSelected())
                item.getStyleClass().add("habit-item-completed");
            else
                item.getStyleClass().remove("habit-item-completed");
            refreshCalendarView();
        });
        return item;
    }

    private void updateStatusLabel() {
        Map<Integer, Boolean> status = habitDAO.getCompletionStatusForDay(selectedDate);
        long completedCount = status.values().stream().filter(b -> b).count();
        long totalCount = habits.size();

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd");
        if (totalCount == 0)
            statusLabel.setText("0/0 Habits. Add a habit!");
        else
            statusLabel.setText(String.format("Progress for %s: %d/%d completed",
                    selectedDate.format(displayFormatter),
                    completedCount, totalCount));
    }

    @FXML
    private void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        refreshCalendarView();
    }

    @FXML
    private void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        refreshCalendarView();
    }

    @FXML
    private void handleLogout() {

    }

    @FXML
    private void handleAdd() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Habit");
        dialog.setHeaderText("Add a new habit");
        dialog.setContentText("Enter habit name:");

        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (!trimmed.isEmpty() && habits.stream().noneMatch(h -> h.getName().equals(trimmed))) {
                boolean saved = habitDAO.addHabit(new Habit(trimmed));
                if (saved) {
                    loadHabits();
                    refreshCalendarView();
                    System.out.println("Added habit: " + trimmed);
                } else
                    System.out.println("Failed to add habit to database.");
            }
        });
    }

    @FXML
    private void handleEdit() {
        if (habits.isEmpty()) {
            System.out.println("No habits to edit");
            return;
        }

        var habitNames = habits.stream().map(Habit::getName).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(habitNames.get(0), habitNames);
        dialog.setTitle("Edit Habit");
        dialog.setHeaderText("Select habit to edit");
        dialog.setContentText("Choose habit:");

        dialog.showAndWait().ifPresent(habitToEditName -> {
            Habit habitToEdit = habits.stream()
                    .filter(h -> h.getName().equals(habitToEditName))
                    .findFirst().orElse(null);
            if (habitToEdit == null) {
                System.out.println("Selected habit not found");
                return;
            }

            TextInputDialog editDialog = new TextInputDialog(habitToEditName);
            editDialog.setTitle("Edit Habit");
            editDialog.setHeaderText("Editing habit: " + habitToEditName);
            editDialog.setContentText("Enter new habit name:");

            editDialog.showAndWait().ifPresent(newName -> {
                String trimmedNewName = newName.trim();
                if (!trimmedNewName.isEmpty() && habits.stream().noneMatch(h -> h.getName().equals(trimmedNewName))) {
                    boolean updated = habitDAO.updateHabit(habitToEdit, trimmedNewName);
                    if (updated) {
                        loadHabits();
                        refreshCalendarView();
                        System.out.println("Edited habit: " + habitToEditName + " to " + trimmedNewName);
                    } else {
                        System.out.println("Failed to update habit in database.");
                    }
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

        var habitNames = habits.stream().map(Habit::getName).toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(habitNames.get(0), habitNames);
        dialog.setTitle("Delete Habit");
        dialog.setHeaderText("Select habit to delete");
        dialog.setContentText("Choose habit:");

        dialog.showAndWait().ifPresent(nameToDelete -> {
            Habit habitToDelete = habits.stream().filter(h -> h.getName().equals(nameToDelete)).findFirst()
                    .orElse(null);
            if (habitToDelete == null) {
                System.out.println("Selected habit not found");
                return;
            }
            if (habitDAO.deleteHabit(habitToDelete)) {
                loadHabits();
                refreshCalendarView();
                System.out.println("Deleted habit: " + nameToDelete);
            } else {
                System.out.println("Failed to delete habit from database.");
            }
        });
    }

    @FXML
    private void handleAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/analytics.fxml"));
            Parent analyticsRoot = loader.load();

            Scene scene = new Scene(analyticsRoot);
            Stage stage = (Stage) monthYearLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Habit Analytics");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
