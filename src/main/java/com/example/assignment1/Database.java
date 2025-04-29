package com.example.assignment1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:skunks.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    userid INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    name TEXT,
                    email TEXT
                );
            """;
            stmt.execute(createUsersTable);

            String createProjectsTable = """
                CREATE TABLE IF NOT EXISTS projects (
                    userid INTEGER NOT NULL,
                    projectid INTEGER PRIMARY KEY AUTOINCREMENT,
                    transcript TEXT

                );
            """;
            stmt.execute(createProjectsTable);

            String createLeaderboardTable = """
                CREATE TABLE IF NOT EXISTS leaderboard (
                    userid INTEGER PRIMARY KEY,
                    score INTEGER DEFAULT 0,
                    FOREIGN KEY (userid) REFERENCES users(userid)
                );
            """;
            stmt.execute(createLeaderboardTable);

            System.out.println("Database and tables successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password, String name, String email) {
        String sql = "INSERT INTO users (username, password, name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // In production, hash the password!
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.executeUpdate();

            // Get the generated user ID
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                initializeLeaderboardEntry(userId);
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void initializeLeaderboardEntry(int userId) {
        String sql = "INSERT INTO leaderboard (userid, score) VALUES (?, 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

    // LEADERBOARD FUNCTIONS:

    public static boolean updateScore(String username, int newScore) {
        String sql = """
            UPDATE leaderboard
            SET score = ?
            WHERE userid = (SELECT userid FROM users WHERE username = ?)
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newScore);
            pstmt.setString(2, username);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getLeaderboard() {
        List<String> leaderboard = new ArrayList<>();
        String sql = """
            SELECT u.username, l.score
            FROM leaderboard l
            JOIN users u ON l.userid = u.userid
            ORDER BY l.score DESC
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String entry = rs.getString("username") + " - " + rs.getInt("score") + " points";
                leaderboard.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

    public static List<String> fillProjectsList(int userid){
        List<String> projects = new ArrayList<>();
        String sql = """
                SELECT projectid
                FROM projects
                WHERE userid = ?
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)){
             pstmt.setInt(1, userid);
             ResultSet rs = pstmt.executeQuery(); {

                while (rs.next()) {
                    String entry = rs.getString("projectid");
                    projects.add(entry);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
}
