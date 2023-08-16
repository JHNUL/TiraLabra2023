package org.juhanir.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.juhanir.Constants;
import org.juhanir.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class UserInterfaceIT {

  @Start
  void setUp(Stage stage) throws IOException {
    new Main().start(stage);
  }

  @Test
  void walkThrough(FxRobot robot) {

    // Starting the app: training data loads
    this.waitUntilSpinnerDisappears(robot, 2000);

    // Training, generating and plaback buttons are all disabled
    this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));

    // Can select a training data key
    String selectedKey = this.selectTrainingDataset(robot).split(" ")[0].strip();

    // Try invalid inputs to degree
    List<String> inputs = List.of("foo", "-3", String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MIN - 1),
        String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MAX + 1), " ");
    this.inputToDegreeFieldAndExpectError(robot, inputs);

    // Set valid input to degree
    this.writeToDegreeField(robot, "2");

    // Train
    this.clickButton(robot, "trainButton");

    // Loading spinner while training
    this.waitUntilSpinnerDisappears(robot, 1000);

    // Training, generating buttons are enabled
    this.checkButtonsShouldBeDisabled(robot, List.of("playButton", "stopButton"));
    this.checkButtonsShouldBeEnabled(robot, List.of("trainButton", "generateButton"));

    // Generate
    this.clickButton(robot, "generateButton");

    // Wait until a file is available and select it
    // this.selectFileForPlayback(robot);
  }

  void writeToDegreeField(FxRobot robot, String input) {
    TextField degreeField = robot.lookup("#degreeField").queryAs(TextField.class);
    degreeField.clear();
    robot.clickOn(degreeField).write(input);
    assertEquals("-fx-border-color: none;", degreeField.getStyle());
    this.checkButtonsShouldBeDisabled(robot, List.of("generateButton", "playButton", "stopButton"));
    this.checkButtonsShouldBeEnabled(robot, List.of("trainButton"));
  }

  void inputToDegreeFieldAndExpectError(FxRobot robot, List<String> inputs) {
    TextField degreeField = robot.lookup("#degreeField").queryAs(TextField.class);
    for (String input : inputs) {
      degreeField.clear();
      robot.clickOn(degreeField).write(input);
      this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));
      assertEquals("-fx-border-color: red;", degreeField.getStyle());
    }
  }

  String selectTrainingDataset(FxRobot robot) {
    ComboBox<String> musicalKeySelect = robot.lookup("#musicalKeySelect").queryComboBox();
    int itemCount = musicalKeySelect.getItems().size();
    assertTrue(itemCount > 0, "Itemcount was 0.");
    String selectedKey = musicalKeySelect.getItems().get(new Random().nextInt(itemCount));
    robot.clickOn(musicalKeySelect).clickOn(selectedKey);
    this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));
    return selectedKey;
  }

  void selectFileForPlayback(FxRobot robot) {
    ComboBox<String> playbackSelect = robot.lookup("#playbackSelect").queryComboBox();
    try {
      WaitForAsyncUtils.waitFor(3000, TimeUnit.MILLISECONDS, () -> {
        return !robot.lookup("#playbackSelect").queryComboBox().getItems().isEmpty();
      });
    } catch (TimeoutException e) {
      fail("Select file timeout");
    }
    System.out.println(playbackSelect.getItems());
  }

  void waitUntilSpinnerDisappears(FxRobot robot, int timeoutMs) {
    try {
      WaitForAsyncUtils.waitFor(timeoutMs, TimeUnit.MILLISECONDS, () -> {
        return !robot.lookup("#progressIndicator").query().isVisible();
      });
    } catch (TimeoutException e) {
      fail(e);
    }
  }

  void checkButtonsShouldBeDisabled(FxRobot robot, List<String> buttonIds) {
    for (String btn : buttonIds) {
      assertTrue(robot.lookup("#" + btn).query().isDisabled());
    }
  }

  void checkButtonsShouldBeEnabled(FxRobot robot, List<String> buttonIds) {
    for (String btn : buttonIds) {
      assertFalse(robot.lookup("#" + btn).query().isDisabled());
    }
  }

  void clickButton(FxRobot robot, String buttonId) {
    Button btn = robot.lookup("#" + buttonId).query();
    if (btn.isDisabled()) {
      fail(String.format("Button %s is disabled and cannot be clicked", buttonId));
    }
    robot.clickOn(btn);
  }

}
