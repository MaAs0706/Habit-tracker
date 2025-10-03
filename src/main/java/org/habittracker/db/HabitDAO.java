package org.habittracker.db;

import org.habittracker.models.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDAO {
    private final Connection conn;

    public HabitDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./habittrackerdb"); // replace with your DB URL
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTables() throws SQLException {
        String habitTable = """
            CREATE TABLE IF NOT EXISTS habit (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(255) NOT NULL
            )
        """;
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

    // ---------------- Habits ----------------
    public void addHabit(Habit habit) {
        String sql = "INSERT INTO habit(name) VALUES(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, habit.getName());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) habit.setId(keys.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateHabit(Habit habit) {
        String sql = "UPDATE habit SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, habit.getName());
            stmt.setInt(2, habit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteHabit(int id) {
        String sql1 = "DELETE FROM habit_completion WHERE habit_id = ?";
        String sql2 = "DELETE FROM habit WHERE id = ?";
        try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt1.setInt(1, id);
            stmt1.executeUpdate();
            stmt2.setInt(1, id);
            stmt2.executeUpdate();
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

    // ---------------- Daily Completion ----------------
    public boolean isCompletedToday(Habit habit, LocalDate date) {
        String sql = "SELECT completed FROM habit_completion WHERE habit_id = ? AND completion_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, habit.getId());
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getBoolean("completed");
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void markCompleted(Habit habit, LocalDate date, boolean completed) {
        String check = "SELECT id FROM habit_completion WHERE habit_id = ? AND completion_date = ?";
        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setInt(1, habit.getId());
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String update = "UPDATE habit_completion SET completed = ? WHERE id = ?";
                try (PreparedStatement upd = conn.prepareStatement(update)) {
                    upd.setBoolean(1, completed);
                    upd.setInt(2, rs.getInt("id"));
                    upd.executeUpdate();
                }
            } else {
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
