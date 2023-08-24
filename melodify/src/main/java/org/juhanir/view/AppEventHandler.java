package org.juhanir.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;
import org.juhanir.Constants;
import org.juhanir.domain.Trie;
import org.juhanir.services.GeneratorService;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;

/**
 * Event handlers for UI elements.
 */
public class AppEventHandler {

  private static final Logger eventHandlerLogger = LogManager.getLogger();
  private final Trie trie;
  private final StringProperty musicalKey;
  private final StringProperty playbackFile;
  private final BooleanProperty isLoading;
  private final StringProperty appMessage;
  private final StringProperty noteDuration;
  private IntegerProperty degree;
  private IntegerProperty melodyLength;
  private BooleanProperty canTrainModel;
  private BooleanProperty canGenerate;
  private BooleanProperty canStartPlayback;

  /**
   * Constructor.
   *
   * @param trie         Trie data structure
   * @param musicalKey   Key the user selected
   * @param playbackFile File the user selected for playback
   * @param isLoading    Flag for drawing the loading spinner
   * @param appMessage   Message to display in the UI
   */
  public AppEventHandler(Trie trie, StringProperty musicalKey,
      StringProperty playbackFile, BooleanProperty isLoading, StringProperty appMessage) {
    this.trie = trie;
    this.musicalKey = musicalKey;
    this.playbackFile = playbackFile;
    this.isLoading = isLoading;
    this.appMessage = appMessage;
    this.degree = new SimpleIntegerProperty();
    this.melodyLength = new SimpleIntegerProperty(Constants.GENERATED_MELODY_DEFAULT_LEN);
    this.canTrainModel = new SimpleBooleanProperty(false);
    this.canStartPlayback = new SimpleBooleanProperty(false);
    this.canGenerate = new SimpleBooleanProperty(false);
    this.noteDuration = new SimpleStringProperty();
  }

  /**
   * Event handler for degree textfield.
   *
   * @param degreeField UI element
   */
  public void handleDegreeField(TextField degreeField) {
    degreeField.setPromptText(
        String.format("min %s max %s", Constants.MARKOV_CHAIN_DEGREE_MIN, Constants.MARKOV_CHAIN_DEGREE_MAX));
    degreeField.textProperty().addListener((observable, oldValue, newValue) -> {
      canGenerate.set(false);
      try {
        int value = Integer.parseInt(newValue.strip());
        if (value < Constants.MARKOV_CHAIN_DEGREE_MIN
            || value > Constants.MARKOV_CHAIN_DEGREE_MAX) {
          throw new Exception();
        }
        degree.set(value);
        degreeField.setStyle("-fx-border-color: none;");
        if (musicalKey.get() != null) {
          canTrainModel.set(true);
        }
      } catch (Exception e) {
        degree.set(0);
        degreeField.setStyle("-fx-border-color: red;");
        canTrainModel.set(false);
      }
    });
  }

  /**
   * Event handler for melody length textfield.
   *
   * @param melodyLengthField UI element
   */
  public void handleMelodyLengthField(TextField melodyLengthField) {
    melodyLengthField.setPromptText(String.format("default %s", Constants.GENERATED_MELODY_DEFAULT_LEN));
    melodyLengthField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        int value = Integer.parseInt(newValue.strip());
        if (value < Constants.GENERATED_MELODY_MIN_LEN || value > Constants.GENERATED_MELODY_MAX_LEN) {
          throw new Exception();
        }
        melodyLength.set(value);
        melodyLengthField.setStyle("-fx-border-color: none;");
      } catch (Exception e) {
        melodyLength.set(Constants.GENERATED_MELODY_DEFAULT_LEN);
        melodyLengthField.setStyle("-fx-border-color: red;");
      }
    });
  }

  /**
   * Event handler for musical key dropdown.
   *
   * @param musicalKeySelect UI element
   */
  public void handleKeySelectChange(ComboBox<String> musicalKeySelect) {
    musicalKeySelect.valueProperty().addListener((observable, oldValue, newValue) -> {
      canGenerate.set(false);
      String key = newValue.split(" ")[0].strip();
      musicalKey.set(key);
      if (Constants.MARKOV_CHAIN_DEGREE_MIN < degree.get()
          && degree.get() > Constants.MARKOV_CHAIN_DEGREE_MAX) {
        canTrainModel.set(false);
      }
    });
  }

  /**
   * Event handler for time signature select.
   *
   * @param noteDurationSelect UI element
   */
  public void handlenoteDurationSelect(ComboBox<String> noteDurationSelect) {
    noteDurationSelect.disableProperty().bind(this.canGenerate.not());
    ObservableList<String> vals = FXCollections.observableList(Constants.noteDurations);
    noteDurationSelect.setItems(vals);
    noteDurationSelect.setValue(vals.get(0));
    noteDuration.set(vals.get(0));
    noteDurationSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
      noteDuration.set(newValue);
    });
  }

  /**
   * Event handler for playback file dropdown.
   *
   * @param playbackSelect UI element
   */
  public void handlePlaybackSelectChange(ComboBox<String> playbackSelect) {
    playbackSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        playbackFile.set(newValue);
        canStartPlayback.set(true);
      } else {
        canStartPlayback.set(false);
      }
    });
  }

  /**
   * Event handler for train button.
   *
   * @param trainButton UI element
   * @param filesPerKey Training data files grouped per musical key
   */
  public void handleTrainButton(Button trainButton, Map<String, List<String>> filesPerKey) {
    trainButton.disableProperty().bind(this.canTrainModel.not());
    trainButton.setOnAction(event -> {
      try {
        this.appMessage.set("");
        List<String> files = filesPerKey.getOrDefault(musicalKey.get(), Collections.emptyList());
        if (files.isEmpty() || degree.get() < Constants.MARKOV_CHAIN_DEGREE_MIN) {
          return;
        }
        this.canGenerate.set(false);
        Task<Void> trainingTask = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            TrainingService trainer = new TrainingService(new FileIo(), new ScoreParser(), trie);
            trie.clear();
            trainer.trainWith(files, degree.get());
            return null;
          }
        };

        trainingTask.setOnFailed(taskEvent -> {
          eventHandlerLogger.error("Failed to train the model", trainingTask.getException());
          this.setErrorMessage(trainingTask.getException(), "Failed to train the model");
        });

        trainingTask.setOnSucceeded(taskEvent -> {
          this.canGenerate.set(true);
        });

        trainingTask.runningProperty().addListener((observable, oldValue, newValue) -> {
          this.isLoading.set(newValue);
        });

        Thread thread = new Thread(trainingTask);
        thread.setDaemon(true);
        thread.start();

      } catch (Exception e) {
        this.setErrorMessage(e, "Failed to train the model");
      }
    });
  }

  /**
   * Event handler for generate button.
   *
   * @param generateButton UI element
   * @param filesPerKey    Training data files grouped per musical key
   * @param playbackFiles  List of generated files
   */
  public void handleGenerateButton(Button generateButton, Map<String, List<String>> filesPerKey,
      ObservableList<String> playbackFiles) {
    generateButton.disableProperty().bind(this.canGenerate.not());
    generateButton.setOnAction(event -> {
      this.appMessage.set("");
      try {
        GeneratorService generator = new GeneratorService(trie, new Random());
        int startingNote = generator.getBaseNoteOfKey(musicalKey.get());
        if (startingNote < 0) {
          this.appMessage
              .set(String.format("ERROR: Could not generate melody starting with %s", this.musicalKey.get()));
        }
        int[] initialSequence = trie.getMostCommonSequenceStartingWith(startingNote, degree.get());
        int[] melody = generator.predictSequence(initialSequence, melodyLength.get());
        if (melody.length < melodyLength.get()) {
          this.appMessage.set(String.format("Generation stopped at %s notes", melody.length));
        }
        String[] fileNames = generator.getGenerationFileNames(musicalKey.get(), degree.get(), noteDuration.get());
        FileIo fileUtil = new FileIo();
        String staccatoString = generator.toStaccatoString(melody, noteDuration.get());
        Pattern pattern = new Pattern(new Pattern(staccatoString));
        fileUtil.writeToFile(Constants.OUTPUT_DATA_PATH, fileNames[0], pattern.getPattern().toString());
        fileUtil.saveMidiFile(Constants.OUTPUT_DATA_PATH, fileNames[1], pattern);
        playbackFiles.add(fileNames[0]);
      } catch (Exception e) {
        eventHandlerLogger.error("Failed to generate melody", e);
        this.setErrorMessage(e, "Failed to generate melody");
      }
    });
  }

  /**
   * Event handler for playback button.
   *
   * @param playButton     UI element
   * @param stopButton     UI element
   * @param innerContainer UI element
   */
  public void handlePlayButton(Button playButton, Button stopButton, VBox innerContainer) {
    playButton.disableProperty().bind(this.canStartPlayback.not());
    stopButton.setDisable(true);
    playButton.setOnAction(event -> {
      this.appMessage.set("");
      try {
        Player player = new Player();
        Task<Void> playbackTask = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            FileIo reader = new FileIo();
            String staccatoString = reader.readFileAsString(Constants.OUTPUT_DATA_PATH, playbackFile.get());
            player.play(new Pattern(staccatoString));
            return null;
          }
        };

        playbackTask.setOnFailed(taskEvent -> {
          eventHandlerLogger.error("Cannot start playback", playbackTask.getException());
          this.setErrorMessage(playbackTask.getException(), "Cannot start playback");
        });

        playbackTask.runningProperty().addListener((observable, oldValue, newValue) -> {
          innerContainer.setDisable(newValue);
        });

        stopButton.disableProperty().bind(playbackTask.runningProperty().not());

        // this is repeatedly set every time playback is started
        // but it creates a new handler fn and the previous
        // is not referenced anymore
        stopButton.setOnAction(stopBtnEvent -> {
          try {
            ManagedPlayer managedPlayer = player.getManagedPlayer();
            if (!managedPlayer.isFinished()) {
              managedPlayer.finish();
            }
          } catch (Exception e) {
            this.setErrorMessage(e, "Failed to stop playback");
          }
        });

        Thread thread = new Thread(playbackTask);
        thread.setDaemon(true);
        thread.start();

      } catch (Exception e) {
        this.setErrorMessage(e, "Cannot start playback");
      }
    });
  }

  /**
   * Event handler for progress indicator.
   *
   * @param spinner   indicator
   * @param container UI element to disable when indicator is showing
   */
  public void handleProgressIndicator(ProgressIndicator spinner, VBox container) {
    spinner.setVisible(false);
    this.isLoading.addListener((observable, oldvalue, newValue) -> {
      spinner.setVisible(newValue);
      container.setDisable(newValue);
    });
  }

  /**
   * Event handler for info label.
   *
   * @param infoLabel UI element
   */
  public void handleInfoLabel(Label infoLabel) {
    this.appMessage.addListener((observable, oldValue, newValue) -> {
      boolean isError = newValue.startsWith("ERROR:");
      String message = isError ? newValue.substring(6).strip() : newValue;
      String style = "-fx-font-style: italic; -fx-padding: 0 0 4 4;";
      if (isError) {
        style += " -fx-text-fill: red;";
      }
      infoLabel.setStyle(style);
      infoLabel.setText(message);
    });
  }

  private void setErrorMessage(Throwable e, String baseMessage) {
    String error = e.getMessage();
    String msg = error != null && !error.isBlank() ? String.format("ERROR: %s: %s", baseMessage, error)
        : String.format("ERROR: %s", baseMessage);
    this.appMessage.set(msg);
  }

}
