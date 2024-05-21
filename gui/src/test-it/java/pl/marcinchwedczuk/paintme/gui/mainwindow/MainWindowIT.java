package pl.marcinchwedczuk.paintme.gui.mainwindow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;
import pl.marcinchwedczuk.paintme.gui.App;

import java.util.concurrent.TimeoutException;

import static org.testfx.assertions.api.Assertions.assertThat;

public class MainWindowIT {
    private FxRobot robot = new FxRobot();

    @BeforeEach
    public void setup() throws Exception {
        ApplicationTest.launch(App.class);
    }

    @AfterEach
    public void cleanup() throws TimeoutException {
        FxToolkit.cleanupStages();
    }

    @Test
    public void application_starts_properly() {
        // Visit https://github.com/TestFX/TestFX
        // to learn how to test.
        Assertions.assertThat(robot.lookup(".button").queryButton())
                .hasText("Click me!");

        Assertions.assertThat(robot.lookup("#label").queryLabeled())
                .hasText("'Hello, world!'");

        robot.clickOn(robot.lookup(".button").queryButton());

        Assertions.assertThat(robot.lookup("#label").queryLabeled())
                .hasText("Clicked!!!");
    }
}