import com.example.assignment1.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest {
    private static Project project1;
    private static Project project2;
    private static Project project3;

    @BeforeEach
    public void setUp() {
        project1 = new Project(1, 13, "Hello mr world");
        project2 = new Project(1, 10, "My beans are noodly");
        project3 = new Project(2, 16, "Whuddup my guy");
    }

    @Test
    public void testGetUserId() {
        int id = project1.getUserid();
        assertEquals(1, id);
    }

    @Test
    public void testSetUserId(){
        project1.setUserid(2);
        int id = project1.getUserid();
        assertEquals(2, id);
    }

    @Test
    public void testGetProjectId(){
        int id = project1.getProjectid();
        assertEquals(13, id);
    }

    @Test
    public void testSetProjectId(){
        project1.setProjectid(2);
        int id = project1.getProjectid();
        assertEquals(2, id);
    }

    @Test
    public void testGetTranscript(){
        String transcript = project1.getTranscript();
        assertEquals("Hello mr world", transcript);
    }

    @Test
    public void testSetTranscript(){
        project1.setTranscript("Hello mr worldly worldy");
        String transcript = project1.getTranscript();
        assertEquals("Hello mr worldly worldy", transcript);
    }
}
