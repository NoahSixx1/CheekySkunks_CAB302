package com.example.assignment1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:skunks.db"; //skunks.db

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
    public static boolean emailExists(String email) {
        // Pseudo‐code: adapt to your actual JDBC or ORM setup
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // or true to be safe on error
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

    public static Integer getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
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

    public static List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
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
                String user = rs.getString("username");
                int score = rs.getInt("score");
                leaderboard.add(new LeaderboardEntry(user, "", score)); // Empty rap for now, modify if needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
    public static class ProjectInfo {
        private final String id;
        private final String name;

        public ProjectInfo(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name; // This makes ListView display the name by default
        }
    }

    public static List<ProjectInfo> fillProjectsList(String userid) {
        List<ProjectInfo> projects = new ArrayList<>();
        String sql = """
        SELECT projectid, name
        FROM projects
        WHERE userid = ?
        ORDER BY projectid DESC
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("projectid");
                String name = rs.getString("name");
                // If name is null or empty, use a default name with the ID
                if (name == null || name.isEmpty()) {
                    name = "Rap #" + id;
                }
                projects.add(new ProjectInfo(id, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }
}
