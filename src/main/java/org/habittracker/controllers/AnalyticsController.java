package org.habittracker.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import javafx.stage.Stage;
import org.habittracker.db.HabitDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

public class AnalyticsController {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private ComboBox<String> rangeSelector;

    private HabitDAO habitDAO;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        habitDAO = new HabitDAO();

        // Default range → This Week
        rangeSelector.setValue("This Week");
        updateCharts();

        // Listener for dropdown change
        rangeSelector.setOnAction(e -> updateCharts());
    }

    private void updateCharts() {
        String range = rangeSelector.getValue();
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end = now;

        switch (range) {
            case "This Week" -> {
                start = now.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            }
            case "This Month" -> {
                start = now.withDayOfMonth(1);
            }
            case "Last 7 Days" -> {
                start = now.minusDays(6);
            }
            case "Last 30 Days" -> {
                start = now.minusDays(29);
            }
            default -> start = now.minusDays(6); // fallback → last 7 days
        }

        loadLineChart(start, end);
        loadPieChart(start, end);
    }

    private void loadLineChart(LocalDate start, LocalDate end) {
        lineChart.getData().clear();

        Map<LocalDate, Integer> dailyCounts = habitDAO.getDailyCompletionCounts(start, end);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Habits Completed");

        LocalDate date = start;
        while (!date.isAfter(end)) {
            int count = dailyCounts.getOrDefault(date, 0);
            series.getData().add(new XYChart.Data<>(date.toString(), count));
            date = date.plusDays(1);
        }

        lineChart.getData().add(series);
    }

    private void loadPieChart(LocalDate start, LocalDate end) {
        pieChart.getData().clear();

        Map<String, Integer> habitCounts = habitDAO.getHabitCompletionCounts(start, end);

        for (Map.Entry<String, Integer> entry : habitCounts.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChart.getData().add(slice);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the Dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/habittracker/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setTitle("Habit Tracker - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading dashboard.fxml");
        }
    }
}
