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
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
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
                    title TEXT DEFAULT 'Untitled Project',
                    transcript TEXT,
                    score INTEGER DEFAULT 0
                );
            """;
            stmt.execute(createProjectsTable);

            String createLeaderboardTable = """
                CREATE TABLE IF NOT EXISTS leaderboard (
                    userid INTEGER,
                    projectid INTEGER,
                    score INTEGER DEFAULT 0,
                    title TEXT,
                    PRIMARY KEY (userid, projectid),
                    FOREIGN KEY (userid) REFERENCES users(id)
                );
            """;
            stmt.execute(createLeaderboardTable);

            System.out.println("Database and tables successfully initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String password, String name, String email) {
        String sql = "INSERT INTO users (username, password, name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password); // ⚠️ hash in production
            pstmt.setString(3, name);
            pstmt.setString(4, email);
            pstmt.executeUpdate();

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
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void initializeLeaderboardEntry(int userId) {
        String sql = "INSERT OR IGNORE INTO leaderboard (userid, score) VALUES (?, 0)";
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
            pstmt.setString(2, password);
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
        return null;
    }

    public static boolean updateScore(String username, int newScore) {
        String sql = """
            UPDATE leaderboard
            SET score = ?
            WHERE userid = (SELECT id FROM users WHERE username = ?)
            """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newScore);
            pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        String sql = """
            SELECT u.username, l.title, l.score
            FROM leaderboard l
            JOIN users u ON l.userid = u.id
            ORDER BY l.score DESC
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String user = rs.getString("username");
                String title = rs.getString("title");
                int score = rs.getInt("score");
                leaderboard.add(new LeaderboardEntry(user, title, score));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaderboard;
    }

    public static List<ProjectEntry> fillProjectsList(String userId) {
        List<ProjectEntry> projects = new ArrayList<>();
        String sql = "SELECT projectid, title FROM projects WHERE userid = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("projectid");
                String title = rs.getString("title");
                projects.add(new ProjectEntry(id, title));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public static boolean updateProjectTranscriptAndScore(int projectId, String transcript, int score, String title) {
        String sql = "UPDATE projects SET transcript = ?, score = ?, title = ? WHERE projectid = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, transcript);
            pstmt.setInt(2, score);
            pstmt.setString(3, title);
            pstmt.setInt(4, projectId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateLeaderboardEntry(int userId, int projectId, int score, String title) {
        String sql = """
            INSERT OR REPLACE INTO leaderboard (userid, projectid, score, title)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, projectId);
            pstmt.setInt(3, score);
            pstmt.setString(4, title);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
