import com.example.assignment1.Database;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    static final String TEST_USERNAME = "testuser123";
    static final String TEST_PASSWORD = "testpass";
    static final String TEST_NAME = "Test User";
    static final String TEST_EMAIL = "test@example.com";

    @BeforeAll
    public static void setup() {
        // Register test user once before all tests
        Database.registerUser(TEST_USERNAME, TEST_PASSWORD, TEST_NAME, TEST_EMAIL);
    }

    @Test
    public void testAuthenticateUser_ValidCredentials() {
        boolean result = Database.authenticateUser(TEST_USERNAME, TEST_PASSWORD);
        assertTrue(result, "User should authenticate with correct credentials");
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials() {
        boolean result = Database.authenticateUser(TEST_USERNAME, "wrongpass");
        assertFalse(result, "User should not authenticate with incorrect password");
    }

    @Test
    public void testGetUserId_ValidUsername() {
        String userId = String.valueOf(Database.getUserId(TEST_USERNAME));
        assertNotNull(userId, "User ID should be returned for valid username");
    }

//    @Test
  //  public void testUpdateScoreAndGetLeaderboard() {
    //    boolean updated = Database.updateScore(TEST_USERNAME, 999);
      //  assertTrue(updated, "Score update should succeed");

        //List<String> leaderboard = Database.getLeaderboard();
        //assertNotNull(leaderboard, "Leaderboard should not be null");
        //assertTrue(
          //      leaderboard.stream().anyMatch(entry -> entry.contains(TEST_USERNAME)),
            //    "Leaderboard should contain the test user"
     //   );
   // }

    @Test
    public void testFillProjectsList_Empty() {
        String userId = String.valueOf(Database.getUserId(TEST_USERNAME));
        assertNotNull(userId, "User ID should not be null");

        List<String> projects = Database.fillProjectsList(userId);
        assertNotNull(projects, "Projects list should not be null");
        assertTrue(projects.isEmpty(), "New user should have no projects");
    }
}
