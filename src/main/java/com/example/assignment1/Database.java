package com.example.assignment1;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:skunks.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    userid INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    name TEXT,
                    email TEXT
                );
            """;
            stmt.execute(sql);
            String projects = """
                CREATE TABLE IF NOT EXISTS users (
                    userid INTEGER,
                    projectName TEXT,
                    projectContent TEXT
                );
            """;
            System.out.println("Database and table successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password, String name, String email) {
        String sql = "INSERT INTO users (username, password, name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // Consider hashing
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // Should match hash
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username); // set the username in the SQL

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return String.valueOf(rs.getInt("id")); // or rs.getString("id") if your id is a text
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        return null; // return null if no user found
    }
}
