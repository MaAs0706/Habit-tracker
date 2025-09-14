package org.habittracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/habit_tracker";
    private static final String DB_USER = System.getenv("HABIT_DB_USER");
    private static final String DB_PASSWORD = System.getenv("HABIT_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}