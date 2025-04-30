import com.example.assignment1.LoginPage;
import com.example.assignment1.Project;
import com.example.assignment1.Session;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javafx.application.Platform;

import java.sql.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class InteractionsTest {



    @BeforeAll
    static void initJFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(latch::countDown); // Initializes JavaFX
        latch.await();
    }


    @Test
    void testToggleModeSwitchesToRegister() {
        LoginPage loginPage = new LoginPage();
        loginPage.isRegisterMode = false;

        loginPage.onToggleButtonClick();

        assertTrue(loginPage.isRegisterMode);
        // I don't know how to get this to work yet
    }




    @Test
    void testAgreeCheckboxEnablesNextButton() {
        LoginPage loginPage = new LoginPage();
        loginPage.agreeCheckBox = new CheckBox();
        loginPage.nextButton = new Button();

        loginPage.agreeCheckBox.setSelected(true);
        loginPage.onAgreeCheckBoxClick();
        assertFalse(loginPage.nextButton.isDisabled());

        loginPage.agreeCheckBox.setSelected(false);
        loginPage.onAgreeCheckBoxClick();
        assertTrue(loginPage.nextButton.isDisabled());
    }


}




