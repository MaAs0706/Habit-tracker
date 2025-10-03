package org.habittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;

import org.habittracker.db.HabitDAO;

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
}
