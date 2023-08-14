package org.juhanir.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.juhanir.Constants;
import org.juhanir.domain.Trie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppEventHandlerTest {

  private Trie trie;
  private IntegerProperty degree;
  private StringProperty musicalKey;
  private StringProperty playbackFile;
  private BooleanProperty isLoading;
  private AppEventHandler handler;

  @BeforeAll
  static void initPlatform() {
    Platform.startup(() -> {
    });
  }

  @AfterAll
  static void closePlatform() {
    Platform.exit();
  }

  @BeforeEach
  void setUp() {
    trie = new Trie();
    degree = new SimpleIntegerProperty(0);
    musicalKey = new SimpleStringProperty("C");
    playbackFile = new SimpleStringProperty("foo.xml");
    isLoading = new SimpleBooleanProperty(false);
    handler = new AppEventHandler(trie, degree, musicalKey, playbackFile, isLoading);
  }

  @Test
  void degreeFieldValidInputSetsDegree() {
    TextField textField = new TextField();
    handler.handleDegreeFieldChange(textField);
    assertEquals(0, degree.get());
    textField.setText("3");
    assertEquals(3, degree.get());
  }

  @Test
  void degreeFieldInValidInputSetsDegreeToZero() {
    TextField textField = new TextField();
    handler.handleDegreeFieldChange(textField);
    textField.setText("3");
    assertEquals(3, degree.get());
    textField.setText("fooobar");
    assertEquals(0, degree.get());
    textField.setText(String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MIN - 1));
    assertEquals(0, degree.get());
    textField.setText(String.valueOf(Constants.MARKOV_CHAIN_DEGREE_MAX + 1));
    assertEquals(0, degree.get());
    textField.setText("3");
    assertEquals(3, degree.get());
  }

  @Test
  void degreeFieldInValidInputDisablesTraining() {
    TextField textField = new TextField();
    Button trainButton = new Button();
    Map<String, List<String>> files = new HashMap<>();
    handler.handleTrainButton(trainButton, files);
    handler.handleDegreeFieldChange(textField);
    assertTrue(trainButton.disableProperty().get());
    textField.setText("3");
    assertFalse(trainButton.disableProperty().get());
    textField.setText("fooo");
    assertTrue(trainButton.disableProperty().get());
  }

  @Test
  void keySelectSetsMusicalKey() {
    ComboBox<String> musicalKeySelect = new ComboBox<>();
    handler.handleKeySelectChange(musicalKeySelect);
    assertEquals("C", musicalKey.get());
    musicalKeySelect.setValue("F#");
    assertEquals("F#", musicalKey.get());
  }

  @Test
  void playbackSelectSetsPlaybackFile() {
    ComboBox<String> playbackSelect = new ComboBox<>();
    handler.handlePlaybackSelectChange(playbackSelect);
    assertEquals("foo.xml", playbackFile.get());
    playbackSelect.setValue("newFile.xml");
    assertEquals("newFile.xml", playbackFile.get());
  }

  @Test
  void showsProgressIndicator() {
    ProgressIndicator spinner = new ProgressIndicator();
    VBox container = new VBox();
    handler.handleProgressIndicator(spinner, container);
    assertFalse(spinner.visibleProperty().get());
    isLoading.set(true);
    assertTrue(spinner.visibleProperty().get());
    isLoading.set(false);
    assertFalse(spinner.visibleProperty().get());
  }
}
