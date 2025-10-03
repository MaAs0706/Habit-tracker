package org.habittracker.models;

import java.time.LocalDate;

public class HabitLog {
    private int habitId;
    private int userId;
    private LocalDate date;
    private boolean isCompleted;

    // The repository primarily uses this constructor when reading from the database
    public HabitLog(int habitId, LocalDate date, boolean isCompleted, int userId) {
        this.habitId = habitId;
        this.userId = userId;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    // --- Getters ---
    public int getHabitId() { return habitId; }
    public int getUserId() { return userId; }
    public LocalDate getDate() { return date; }
    public boolean isCompleted() { return isCompleted; }

    // --- Setters (less common for a log, but kept for completeness) ---
    public void setHabitId(int habitId) { this.habitId = habitId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
}