package org.habittracker.db;

import org.habittracker.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Print actual reason
            System.err.println("Error while saving user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User findUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}