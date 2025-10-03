package org.habittracker.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;

    static {
        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources!");
            }
            prop.load(input);

            // These properties must be set to your MySQL details
            DB_URL = prop.getProperty("DB_URL");
            DB_USER = prop.getProperty("DB_USER");
            DB_PASSWORD = prop.getProperty("DB_PASSWORD");

            // Manually register the driver if needed, though DriverManager usually finds it
            // Class.forName("com.mysql.cj.jdbc.Driver"); 

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load DB configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}