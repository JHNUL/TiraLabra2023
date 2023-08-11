package org.juhanir.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.audiveris.proxymusic.ScorePartwise;
import org.jfugue.integration.MusicXmlParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.ManagedPlayer;
import org.jfugue.player.Player;
import org.juhanir.domain.Trie;
import org.juhanir.services.GeneratorService;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.Constants;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;
import org.staccato.StaccatoParserListener;

/**
 * Event handlers for UI elements.
 */
public class AppEventHandler {

  private final Trie trie;
  private final IntegerProperty degree;
  private final StringProperty musicalKey;
  private final StringProperty playbackFile;
  private final BooleanProperty isLoading;
  private BooleanProperty canTrainModel;
  private BooleanProperty isModelTrained;
  private BooleanProperty canStartPlayback;

  /**
   * Constructor.
   *
   * @param trie Trie data structure
   * @param degree Markov Chain degree
   * @param musicalKey Key the user selected
   * @param playbackFile File the user selected for playback
   */
  public AppEventHandler(Trie trie, IntegerProperty degree, StringProperty musicalKey,
      StringProperty playbackFile, BooleanProperty isLoading) {
    this.trie = trie;
    this.degree = degree;
    this.musicalKey = musicalKey;
    this.playbackFile = playbackFile;
    this.isLoading = isLoading;
    this.canTrainModel = new SimpleBooleanProperty(false);
    this.canStartPlayback = new SimpleBooleanProperty(false);
    this.isModelTrained = new SimpleBooleanProperty(false);
  }

  /**
   * Event handler for degree textfield.
   *
   * @param degreeField UI element
   */
  public void handleDegreeFieldChange(TextField degreeField) {
    degreeField.textProperty().addListener((observable, oldValue, newValue) -> {
      // TODO: disable generate button if these are changed
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
   * Event handler for musical key dropdown.
   *
   * @param musicalKeySelect UI element
   */
  public void handleKeySelectChange(ComboBox<String> musicalKeySelect) {
    musicalKeySelect.valueProperty().addListener((observable, oldValue, newValue) -> {
      String key = newValue.split(" ")[0].strip();
      musicalKey.set(key);
      if (Constants.MARKOV_CHAIN_DEGREE_MIN < degree.get()
          && degree.get() > Constants.MARKOV_CHAIN_DEGREE_MAX) {
        canTrainModel.set(false);
      }
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
        List<String> files = filesPerKey.getOrDefault(musicalKey.get(), Collections.emptyList());
        if (files.isEmpty() || degree.get() < Constants.MARKOV_CHAIN_DEGREE_MIN) {
          return;
        }
        this.isModelTrained.set(false);
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
          // TODO: some UI effect on failure
        });

        trainingTask.setOnSucceeded(taskEvent -> {
          this.isModelTrained.set(true);
          // TODO: some UI effect on success
        });

        trainingTask.runningProperty().addListener((observable, oldValue, newValue) -> {
          this.isLoading.set(newValue);
        });

        Thread thread = new Thread(trainingTask);
        thread.setDaemon(true);
        thread.start();

      } catch (Exception e) {
        // TODO: report to the UI
        System.out.println(e);
      }
    });
  }

  /**
   * Event handler for generate button.
   *
   * @param generateButton UI element
   * @param filesPerKey Training data files grouped per musical key
   */
  public void handleGenerateButton(Button generateButton, Map<String, List<String>> filesPerKey,
      ObservableList<String> playbackFiles) {
    generateButton.disableProperty().bind(this.isModelTrained.not());
    generateButton.setOnAction(event -> {
      if (degree.get() < Constants.MARKOV_CHAIN_DEGREE_MIN) {
        return;
      }
      try {
        GeneratorService generator = new GeneratorService(trie, new Random());
        int startingNote = generator.getBaseNoteOfKey(musicalKey.get());
        // Should not be possible in practice to not have any base note of the key (e.g. songs in
        // C major without any C notes), but cannot be guaranteed so default to any random sequence.
        int[] initialSequence =
            startingNote >= 0 ? trie.getRandomSequenceStartingWith(startingNote, degree.get())
                : trie.getRandomSequence(degree.get());
        ScoreParser parser = new ScoreParser();
        int[] melody = generator.predictSequence(initialSequence, 50);
        ScorePartwise score = parser.convertMelodyToScorePartwise(melody, musicalKey.get());
        LocalDateTime now = LocalDateTime.now();
        String fileName = musicalKey.get() + "_generation_"
            + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS"));
        FileIo reader = new FileIo();
        reader.writeToFile(Constants.OUTPUT_DATA_PATH, fileName + ".xml", score);
        playbackFiles.add(fileName + ".xml");
        reader.writeToFile(Constants.OUTPUT_DATA_PATH, fileName, Arrays.toString(melody));
      } catch (Exception e) {
        // TODO: report to the UI
        System.out.println(e);
      }
    });
  }

  /**
   * Event handler for playback button.
   *
   * @param playButton UI element
   */
  public void handlePlayButton(Button playButton, Button stopButton, VBox innerContainer) {
    playButton.disableProperty().bind(this.canStartPlayback.not());
    stopButton.setDisable(true);
    playButton.setOnAction(event -> {
      try {
        Player player = new Player();
        Task<Void> playbackTask = new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            updateMessage("Playing generated file");
            MusicXmlParser mxmlParser = new MusicXmlParser();
            StaccatoParserListener listener = new StaccatoParserListener();
            FileIo reader = new FileIo();
            mxmlParser.addParserListener(listener);
            mxmlParser.parse(reader.readFile(Constants.OUTPUT_DATA_PATH, playbackFile.get()));
            Pattern staccatoPattern = listener.getPattern();
            player.play(staccatoPattern);
            updateMessage("");
            return null;
          }
        };

        playbackTask.setOnSucceeded(taskEvent -> {
          // TODO: some UI effect on success
        });

        playbackTask.messageProperty().addListener((observable, oldValue, newValue) -> {
          // TODO: some UI effect on message
        });

        playbackTask.runningProperty().addListener((observable, oldValue, newValue) -> {
          innerContainer.setDisable(newValue);
        });

        stopButton.disableProperty().bind(playbackTask.runningProperty().not());

        stopButton.setOnAction(stopBtnEvent -> {
          ManagedPlayer managedPlayer = player.getManagedPlayer();
          if (!managedPlayer.isFinished()) {
            managedPlayer.finish();
          }
        });

        Thread thread = new Thread(playbackTask);
        thread.setDaemon(true);
        thread.start();

      } catch (Exception e) {
        // TODO: report to the UI
        System.out.println(e);
      }
    });
  }

  /**
   * Event handler for progress indicator.
   *
   * @param spinner indicator
   * @param container UI element to disable when indicator is showing
   */
  public void handleProgressIndicator(ProgressIndicator spinner, VBox container) {
    this.isLoading.addListener((observable, oldvalue, newValue) -> {
      spinner.setVisible(newValue);
      container.setDisable(newValue);
    });
  }

}
