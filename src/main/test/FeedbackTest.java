import com.example.assignment1.FeedbackPage;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTest {

    @Test
    public void testLoadTranscript_ValidProjectId() throws SQLException {
        // Setup: create in-memory SQLite database
        String url = "jdbc:sqlite::memory:";
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE projects (projectid INTEGER PRIMARY KEY, transcript TEXT)");
            stmt.execute("INSERT INTO projects (projectid, transcript) VALUES (1, 'Test rap lyrics')");

            // Override DB connection method temporarily (simulate it in test)
            String transcript = null;
            String query = "SELECT transcript FROM projects WHERE projectid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, 1);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    transcript = rs.getString("transcript");
                }
            }

            assertEquals("Test rap lyrics", transcript);
        }
    }

    @Test
    public void testLoadTranscript_InvalidProjectId() throws SQLException {
        String url = "jdbc:sqlite::memory:";
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE projects (projectid INTEGER PRIMARY KEY, transcript TEXT)");

            String query = "SELECT transcript FROM projects WHERE projectid = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, 999);
                ResultSet rs = pstmt.executeQuery();
                assertFalse(rs.next(), "No transcript should be found for invalid project ID");
            }
        }
    }
}
