import com.example.assignment1.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private static User user1;
    private static User user2;
    private static User user3;

    @BeforeEach
    public void Setup(){
        user1 = new User(1, "noah", "123", "noah", "noahsixx@gmail.com");
        user2 = new User(2, "nsixx", "noah", "noah", "noah@example.com");
        user3 = new User(3, "defaultUser", "password", "name", "email@email.com");
    }

    @Test
    public void testGetUserId(){
        int id = user1.getId();
        assertEquals(1, id);
    }

    @Test
    public void testSetUserId(){
        user1.setId(2);
        int id = user1.getId();
        assertEquals(2, id);
    }

    @Test
    public void testGetUsername(){
        String username = user1.getUsername();
        assertEquals("noah", username);
    }

    @Test
    public void testSetUsername(){
        user1.setUsername("mr beans");
        String username = user1.getUsername();
        assertEquals("mr beans", username);
    }

    @Test
    public void testGetPassword(){
        String password = user1.getPassword();
        assertEquals("123", password);
    }

    @Test
    public void testSetPassword(){
        user1.setPassword("456");
        String password = user1.getPassword();
        assertEquals("456", password);
    }

    @Test
    public void testGetName(){
        String name = user1.getName();
        assertEquals("noah", name);
    }

    @Test
    public void testSetName(){
        user1.setName("sixx");
        String name = user1.getName();
        assertEquals("sixx", name);
    }

    @Test
    public void testGetEmail(){
        String email = user1.getEmail();
        assertEquals("noahsixx@gmail.com", email);
    }

    @Test
    public void testSetEmail(){
        user1.setEmail("noah@noah.com");
        String email = user1.getEmail();
        assertEquals("noah@noah.com", email);
    }
}
