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

        // Add day headers (Mon–Sun)
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Label lbl = new Label(days[i]);
            lbl.setStyle("-fx-font-weight: bold;");
            calendarGrid.add(lbl, i, 0); // row 0 = header row
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
        Rectangle rect = new Rectangle(80, 60);
        rect.setFill(Color.LIGHTGRAY);
        rect.setStroke(Color.BLACK);

        Text dayText = new Text(String.valueOf(day));

        StackPane cell = new StackPane(rect, dayText);

        LocalDate cellDate = LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), day);

        // Highlight today
        if (cellDate.equals(LocalDate.now())) {
            rect.setFill(Color.LIGHTGREEN);
        }

        cell.setOnMouseClicked(e -> {
            System.out.println("Clicked day: " + cellDate);
            // Later: Show completed habits for this date
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

    @FXML
    private void handleAnalytics(){
        System.out.println("Transfer to Analytics");
        //TODO: transfer the user to the analytics page
    }
}
