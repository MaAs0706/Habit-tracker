package org.habittracker.db;

import org.habittracker.models.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitDAO {
    private Connection conn;

    public HabitDAO() {
        try {
            // Get connection from the utility class which reads config.properties
            conn = DatabaseConnection.getConnection();
            System.out.println("✅ Connected to DB: habit_tracker"); // Confirmation print
            createTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed. Check config.properties and MySQL server status.");
            throw new RuntimeException("Failed to establish database connection or create tables.", e);
        }
    }

    private void createTables() throws SQLException {
        // Using AUTO_INCREMENT for MySQL
        String habitTable = """
            CREATE TABLE IF NOT EXISTS habit (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL
            )
        """;
        // Using BOOL or TINYINT(1) for boolean in MySQL
        String completionTable = """
            CREATE TABLE IF NOT EXISTS habit_completion (
                id INT PRIMARY KEY AUTO_INCREMENT,
                habit_id INT NOT NULL,
                completion_date DATE NOT NULL,
                completed BOOLEAN NOT NULL DEFAULT FALSE,
                FOREIGN KEY (habit_id) REFERENCES habit(id)
            )
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(habitTable);
            stmt.execute(completionTable);
        }
    }

    // ---------------- HABIT CRUD METHODS ----------------
    
    public void addHabit(Habit habit) {
        String sql = "INSERT INTO habit(name) VALUES(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, habit.getName());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) habit.setId(keys.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habit";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                habits.add(new Habit(rs.getInt("id"), rs.getString("name"), false)); 
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return habits;
    }
    
    // ---------------- COMPLETION METHODS ----------------

    public Map<Integer, Boolean> getCompletionStatusForDay(LocalDate date) {
        Map<Integer, Boolean> status = new HashMap<>();
        String sql = "SELECT habit_id, completed FROM habit_completion WHERE completion_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    status.put(rs.getInt("habit_id"), rs.getBoolean("completed")); 
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return status;
    }

    public void markCompleted(Habit habit, LocalDate date, boolean completed) {
        String check = "SELECT id FROM habit_completion WHERE habit_id = ? AND completion_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setInt(1, habit.getId());
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Update existing record
                String update = "UPDATE habit_completion SET completed = ? WHERE id = ?";
                try (PreparedStatement upd = conn.prepareStatement(update)) {
                    upd.setBoolean(1, completed);
                    upd.setInt(2, rs.getInt("id"));
                    upd.executeUpdate();
                }
            } else {
                // Insert new record
                String insert = "INSERT INTO habit_completion(habit_id, completion_date, completed) VALUES (?, ?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(insert)) {
                    ins.setInt(1, habit.getId());
                    ins.setDate(2, java.sql.Date.valueOf(date));
                    ins.setBoolean(3, completed);
                    ins.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}