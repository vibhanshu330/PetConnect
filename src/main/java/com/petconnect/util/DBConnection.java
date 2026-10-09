package com.petconnect.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central utility for obtaining JDBC connections to the PetConnect database.
 * Configure DB_URL, DB_USER, and DB_PASSWORD in the environment of the JVM
 * running the application (for example, the IDE or Tomcat process).
 */
public class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/petconnect?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = valueOrDefault("DB_URL", DEFAULT_URL);
        String username = requiredEnvironmentVariable("DB_USER");
        String password = requiredEnvironmentVariable("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Check the Maven dependency.", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String valueOrDefault(String name, String defaultValue) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String requiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Missing required environment variable " + name
                    + ". Configure DB_URL (optional), DB_USER, and DB_PASSWORD for the application JVM.");
        }
        return value;
    }
}
