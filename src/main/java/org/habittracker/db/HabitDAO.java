package org.habittracker.db;

import org.habittracker.models.Habit;
import org.habittracker.services.GoogleCalendarService;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class HabitDAO {
    private Connection conn;

    public HabitDAO() {
        try {
            conn = DatabaseConnection.getConnection();
            System.out.println("✅ Connected to DB: habit_tracker");
            createTables();
        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            throw new RuntimeException("Failed to establish DB connection or create tables.", e);
        }
    }

    private void createTables() throws SQLException {
        String habitTable = """
                CREATE TABLE IF NOT EXISTS habit (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    google_event_id VARCHAR(255)  -- 🔹 new column
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

    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habit";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Habit h = new Habit(rs.getInt("id"), rs.getString("name"), false);
                h.setGoogleEventId(rs.getString("google_event_id")); // 🔹 add this
                habits.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habits;
    }

    public boolean addHabit(Habit habit) {
        String sql = "INSERT INTO habit (name, google_event_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 🔹 Step 1: Create Google Calendar event first
            String eventId = null;
            try {
                eventId = GoogleCalendarService.addEvent(
                        habit.getName(),
                        "Reminder for habit: " + habit.getName(),
                        new java.util.Date(), // current time
                        60 // duration: 60 min
                );
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                System.out.println("⚠️ Failed to create Google Calendar event.");
            }

            habit.setGoogleEventId(eventId);

            // 🔹 Step 2: Store in database
            stmt.setString(1, habit.getName());
            stmt.setString(2, eventId);
            int affected = stmt.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateHabit(Habit habit, String newName) {
        String sql = "UPDATE habit SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setInt(2, habit.getId());
            int affected = stmt.executeUpdate();

            // 🔹 Update Google Calendar event too
            if (habit.getGoogleEventId() != null) {
                try {
                    GoogleCalendarService.updateEvent(
                            habit.getGoogleEventId(),
                            newName,
                            "Updated habit: " + newName
                    );
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                    System.out.println("⚠️ Failed to update Google Calendar event.");
                }
            }

            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteHabit(Habit habit) {
        String deleteCompletions = "DELETE FROM habit_completion WHERE habit_id = ?";
        String deleteHabit = "DELETE FROM habit WHERE id = ?";
        try {
            conn.setAutoCommit(false);

            // 🔹 Delete completions
            try (PreparedStatement stmt = conn.prepareStatement(deleteCompletions)) {
                stmt.setInt(1, habit.getId());
                stmt.executeUpdate();
            }

            // 🔹 Delete from Google Calendar
            if (habit.getGoogleEventId() != null) {
                try {
                    GoogleCalendarService.deleteEvent(habit.getGoogleEventId());
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                    System.out.println("⚠️ Failed to delete Google Calendar event.");
                }
            }

            // 🔹 Delete from local DB
            int affected;
            try (PreparedStatement stmt = conn.prepareStatement(deleteHabit)) {
                stmt.setInt(1, habit.getId());
                affected = stmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    // Existing analytics and markCompleted methods remain unchanged
    // ⬇️ (no need to modify)

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

    public Map<LocalDate, Integer> getDailyCompletionCounts(LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> counts = new HashMap<>();
        String sql = """
        SELECT completion_date, COUNT(*) AS total
        FROM habit_completion
        WHERE completed = TRUE
          AND completion_date BETWEEN ? AND ?
        GROUP BY completion_date
        ORDER BY completion_date
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getDate("completion_date").toLocalDate(),
                            rs.getInt("total"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    public Map<String, Integer> getHabitCompletionCounts(LocalDate start, LocalDate end) {
        Map<String, Integer> counts = new HashMap<>();
        String sql = """
        SELECT h.name, COUNT(*) AS total
        FROM habit_completion hc
        JOIN habit h ON hc.habit_id = h.id
        WHERE hc.completed = TRUE
          AND hc.completion_date BETWEEN ? AND ?
        GROUP BY h.name
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("name"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
