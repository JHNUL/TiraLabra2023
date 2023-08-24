package org.juhanir.view;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.juhanir.Constants;
import org.juhanir.Launcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
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
    new Launcher().start(stage);
  }

  @Test
  void walkThrough(FxRobot robot) {

    // Starting the app: training data loads
    this.waitUntilSpinnerDisappears(robot, 2000);

    // Training, generating and plaback buttons are all disabled
    this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));

    // Can select a training data key
    this.selectTrainingDataset(robot);

    // Try invalid inputs to degree
    List<String> inputs = List.of("foo", "-3", String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MIN - 1),
        String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MAX + 1), " ", "2.3", "1e1");
    this.inputToFieldAndExpectError(robot, inputs, "degreeField");
    this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));

    // Set valid input to degree
    this.writeToInputField(robot, "2", "degreeField");
    this.checkButtonsShouldBeDisabled(robot, List.of("generateButton", "playButton", "stopButton"));
    this.checkButtonsShouldBeEnabled(robot, List.of("trainButton"));

    // Train
    this.clickButton(robot, "trainButton");

    // Loading spinner while training
    this.waitUntilSpinnerDisappears(robot, 1000);

    // Try invalid inputs to melody len
    List<String> melodyLenInputs = List.of("foo", "-3", String.valueOf(Constants.GENERATED_MELODY_MIN_LEN - 1),
        String.valueOf(Constants.GENERATED_MELODY_MAX_LEN + 1), " ", "20.3", "1e10");
    this.inputToFieldAndExpectError(robot, melodyLenInputs, "melodyLengthField");

    // Set valid input to melody length
    this.writeToInputField(robot, "60", "melodyLengthField");
    this.checkButtonsShouldBeDisabled(robot, List.of("playButton", "stopButton"));
    this.checkButtonsShouldBeEnabled(robot, List.of("trainButton", "generateButton"));

    // Generate
    this.waitForButtonToBeEnabled(robot, "generateButton");
    this.selectTimeSignature(robot, "quarter");
    this.clickButton(robot, "generateButton");

    // Change time signature and generate again
    this.selectTimeSignature(robot, "eighth");
    this.clickButton(robot, "generateButton");

    // Wait until a file is available and select it
    this.selectFileForPlayback(robot);

    // Training, generating buttons are enabled
    this.checkButtonsShouldBeDisabled(robot, List.of("stopButton"));
    this.checkButtonsShouldBeEnabled(robot, List.of("trainButton", "generateButton", "playButton"));

    // Play button can be pressed
    this.clickButton(robot, "playButton");

  }

  void writeToInputField(FxRobot robot, String input, String id) {
    robot.lookup("#" + id).queryAs(TextField.class).clear();
    robot.clickOn("#" + id).write(input);
    Assertions.assertThat(robot.lookup("#" + id).queryAs(TextField.class)).hasStyle("-fx-border-color: none;");
  }

  void inputToFieldAndExpectError(FxRobot robot, List<String> inputs, String id) {
    for (String input : inputs) {
      robot.lookup("#" + id).queryAs(TextField.class).clear();
      robot.clickOn("#" + id).write(input);
      Assertions.assertThat(robot.lookup("#" + id).queryAs(TextField.class)).hasStyle("-fx-border-color: red;");
    }
  }

  void selectTrainingDataset(FxRobot robot) {
    ComboBox<String> musicalKeySelect = robot.lookup("#musicalKeySelect").queryComboBox();
    int itemCount = musicalKeySelect.getItems().size();
    assertTrue(itemCount > 0, "Itemcount was 0.");
    String selectedKey = musicalKeySelect.getItems().get(new Random().nextInt(itemCount));
    robot.clickOn(musicalKeySelect).clickOn(selectedKey);
    this.checkButtonsShouldBeDisabled(robot, List.of("trainButton", "generateButton", "playButton", "stopButton"));
  }

  void selectFileForPlayback(FxRobot robot) {
    ComboBox<String> playbackSelect = robot.lookup("#playbackSelect").queryComboBox();
    try {
      WaitForAsyncUtils.waitFor(1000, TimeUnit.MILLISECONDS, () -> {
        return !robot.lookup("#playbackSelect").queryComboBox().getItems().isEmpty();
      });
    } catch (TimeoutException e) {
      fail("Select file timeout");
    }
    String file = playbackSelect.getItems().get(playbackSelect.getItems().size() - 1);
    robot.clickOn("#playbackSelect").clickOn(file);
  }

  void selectTimeSignature(FxRobot robot, String ts) {
    robot.clickOn("#noteDurationSelect").clickOn(ts);
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

  void waitForButtonToBeEnabled(FxRobot robot, String buttonId) {
    try {
      WaitForAsyncUtils.waitFor(2000, TimeUnit.MILLISECONDS, () -> {
        return !robot.lookup("#" + buttonId).query().isDisabled();
      });
    } catch (TimeoutException e) {
      fail(e);
    }
  }

  void checkButtonsShouldBeDisabled(FxRobot robot, List<String> buttonIds) {
    for (String btn : buttonIds) {
      Assertions.assertThat(robot.lookup("#" + btn).queryAs(Button.class)).isDisabled();
    }
  }

  void checkButtonsShouldBeEnabled(FxRobot robot, List<String> buttonIds) {
    for (String btn : buttonIds) {
      Assertions.assertThat(robot.lookup("#" + btn).queryAs(Button.class)).isEnabled();
    }
  }

  void clickButton(FxRobot robot, String buttonId) {
    Assertions.assertThat(robot.lookup("#" + buttonId).queryAs(Button.class)).isEnabled();
    robot.lookup("#" + buttonId).queryAs(Button.class).fire();
  }

}
