package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage; 

import org.habittracker.db.HabitDAO;
import org.habittracker.models.Habit; 

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardController {

    // FXML Injections
    @FXML private Label monthYearLabel;
    @FXML private VBox habitsContainer;
    @FXML private GridPane calendarGrid;
    @FXML private Label statusLabel;
    @FXML private TextField newHabitField; // Not strictly used with the Dialog, but injected for completeness
    
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
    
    // ---------------- PRIVATE UTILITY METHODS ----------------

    private void loadHabits() {
        habits = habitDAO.getAllHabits();
        // If no habits, create some defaults
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
            if (!dayStatus.isEmpty()) { 
                dailyHabitStatus.put(dayDate, dayStatus);
            }
        }
    }

    private void setupCalendar(LocalDate date) {
        calendarGrid.getChildren().clear();
        calendarGrid.getRowConstraints().clear(); 

        YearMonth yearMonth = YearMonth.from(date);
        monthYearLabel.setText(yearMonth.format(MONTH_YEAR_FORMATTER));

        LocalDate calendarStart = yearMonth.atDay(1);
        int dayOfWeek = calendarStart.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)
        
        // Adjust for a Monday-start calendar: Mon=0, Tue=1, ..., Sun=6
        int offset = dayOfWeek - 1; 
        if (dayOfWeek == 7) offset = 6; // If Sunday is the first day of the month

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
    
    private void selectDay(LocalDate date) {
        selectedDate = date;
        refreshHabitList(date);
        refreshCalendarView(); // Force a calendar redraw to update selection style
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

    // FIX 1: Corrected logic to apply 'missed' (red) color correctly.
    private void updateCellColor(LocalDate date, StackPane cell) {
        cell.getStyleClass().removeAll("day-cell-complete", "day-cell-partial", "day-cell-missed", "today-cell", "selected-cell");
        
        if (!cell.getStyleClass().contains("day-cell")) {
            cell.getStyleClass().add("day-cell");
        }

        long totalHabits = habits.size();
        
        if (totalHabits > 0) {
            Map<Integer, Boolean> status = dailyHabitStatus.getOrDefault(date, new HashMap<>()); 
            long completedCount = status.values().stream().filter(s -> s).count();

            if (completedCount == totalHabits) {
                cell.getStyleClass().add("day-cell-complete"); 
            } else if (completedCount > 0) {
                cell.getStyleClass().add("day-cell-partial"); 
            } else if (date.isBefore(LocalDate.now())) {
                // Apply missed color only if in the past AND 0 habits completed
                cell.getStyleClass().add("day-cell-missed"); 
            }
        }
        
        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today-cell");
        }
        
        // FIX 2: Ensure selected cell style is applied last and correctly
        if (date.equals(selectedDate) && !YearMonth.from(date).equals(YearMonth.from(currentDate))) {
             // If selected day is NOT in the currently viewed month, don't highlight strongly
        } else if (date.equals(selectedDate)) {
             cell.getStyleClass().add("selected-cell");
        }
    }
    
    private void refreshCalendarView() {
        loadMonthCompletionStatus(currentDate);
        setupCalendar(currentDate);
        refreshHabitList(selectedDate);
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
    
    // FIX 3: Corrected list item creation to use CheckBox as seen in Image 2
    private HBox createHabitItem(Habit habit, boolean completed) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.getStyleClass().add("habit-item");
        
        // CheckBox is used for the toggle and holds the text (as seen in Image 2)
        CheckBox checkBox = new CheckBox(habit.getName());
        checkBox.setSelected(completed);
        
        // Apply initial style to the container
        if (completed) {
            item.getStyleClass().add("habit-item-completed");
        }
        
        item.getChildren().add(checkBox);

        // Set the action to update the database and the UI style
        checkBox.setOnAction(e -> {
            boolean isChecked = checkBox.isSelected();
            habitDAO.markCompleted(habit, selectedDate, isChecked);
            
            // Dynamically update the HBox style
            if (isChecked) {
                item.getStyleClass().add("habit-item-completed");
            } else {
                item.getStyleClass().remove("habit-item-completed");
            }
            
            // This updates the status label AND the calendar grid color
            refreshCalendarView(); 
        });
        
        return item;
    }
    
    // FIX 4: Update status label to show the correct selected date
    private void updateStatusLabel() {
        Map<Integer, Boolean> status = habitDAO.getCompletionStatusForDay(selectedDate);
        long completedCount = status.values().stream().filter(s -> s).count();
        long totalCount = habits.size();
        
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd");

        if (totalCount == 0) {
            statusLabel.setText("0/0 Habits. Add a habit!");
        } else {
            statusLabel.setText(String.format(
                "Progress for %s: %d/%d completed", 
                selectedDate.format(displayFormatter), 
                completedCount, 
                totalCount
            ));
        }
    }

    // ---------------- UI EVENT HANDLERS ----------------

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
    
    // Logic for adding a habit (uses a Dialog since no TextField is globally visible)
    private void handleAddHabitLogic() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Habit");
        dialog.setHeaderText("Enter the name of your new habit:");
        dialog.setContentText("Habit Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Habit newHabit = new Habit(name.trim());
                habitDAO.addHabit(newHabit); 
                loadHabits(); 
                refreshCalendarView(); 
            }
        });
    }

    // HANDLER for FXML onAction="#handleAdd"
    @FXML
    private void handleAdd() {
        handleAddHabitLogic(); 
    }
    
    // HANDLER for FXML onAction="#handleEdit"
    @FXML
    private void handleEdit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Edit functionality not yet implemented.");
        alert.showAndWait();
    }
    
    // HANDLER for FXML onAction="#handleDelete"
    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Delete functionality not yet implemented.");
        alert.showAndWait();
    }

    // HANDLER for FXML onAction="#handleLogout"
    @FXML
    private void handleLogout() {
        if (monthYearLabel != null && monthYearLabel.getScene() != null) {
            Stage stage = (Stage) monthYearLabel.getScene().getWindow();
            stage.close();
        }
    }
}