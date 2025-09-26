package org.habittracker.db;

import org.habittracker.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    // Save a new user
    public boolean saveUser(User user) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // already hashed by AuthService

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error while saving user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Find user by username
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Return user with stored (hashed) password
                return new User(
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error while finding user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
