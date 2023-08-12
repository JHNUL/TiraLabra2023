package org.juhanir.view;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.juhanir.Constants;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;

/**
 * Controllers for UI elements.
 */
public class AppController {

  @FXML
  private TextField degreeField;

  @FXML
  private ComboBox<String> musicalKeySelect;

  @FXML
  private Label infoLabel;

  @FXML
  private Button trainButton;

  @FXML
  private Button generateButton;

  @FXML
  private Button playButton;

  @FXML
  private Button stopButton;

  @FXML
  private ComboBox<String> playbackSelect;

  @FXML
  private VBox container;

  @FXML
  private VBox innerContainer;

  @FXML
  private ProgressIndicator progressIndicator;

  private ObservableList<String> keys =
      FXCollections.observableList(FXCollections.observableArrayList());
  private ObservableList<String> playbackFiles =
      FXCollections.observableList(FXCollections.observableArrayList());
  private IntegerProperty degree = new SimpleIntegerProperty();
  private StringProperty musicalKey = new SimpleStringProperty();
  private StringProperty playbackFile = new SimpleStringProperty();
  private BooleanProperty isLoading = new SimpleBooleanProperty(false);
  private MapProperty<String, List<String>> filesPerKey = new SimpleMapProperty<>();
  private AppEventHandler eventHandler;
  private final Trie trie;

  public AppController() {
    this.trie = new Trie();
    this.eventHandler = new AppEventHandler(trie, degree, musicalKey, playbackFile, isLoading);
  }

  @FXML
  private void initialize() {
    this.eventHandler.handleDegreeFieldChange(this.degreeField);
    this.eventHandler.handleKeySelectChange(this.musicalKeySelect);
    this.eventHandler.handleTrainButton(this.trainButton, this.filesPerKey);
    this.eventHandler.handleGenerateButton(this.generateButton, this.filesPerKey,
        this.playbackFiles);
    this.eventHandler.handlePlayButton(this.playButton, this.stopButton, this.innerContainer);
    this.eventHandler.handlePlaybackSelectChange(this.playbackSelect);
    this.eventHandler.handleProgressIndicator(this.progressIndicator, this.container);
    this.groupDataByKey();
  }

  private void groupDataByKey() {

    Task<Map<String, List<String>>> bgTask = new Task<Map<String, List<String>>>() {
      @Override
      protected Map<String, List<String>> call() throws Exception {
        updateMessage("Reading training data");
        FileIo reader = new FileIo();
        List<String> sourceFiles = reader.getAllFilePathsInFolder(Constants.TRAINING_DATA_PATH);
        List<String> generatedFiles =
            reader.getAllFilePathsInFolder(Constants.OUTPUT_DATA_PATH, ".xml").stream()
                .map(filePath -> filePath.substring(filePath.lastIndexOf(File.separator) + 1))
                .collect(Collectors.toList());
        ScoreParser parser = new ScoreParser();
        Map<String, List<String>> fileMap = parser.collectFilesPerKey(reader, sourceFiles);
        fileMap.put("generatedFiles", generatedFiles);
        updateMessage("");
        return fileMap;
      }
    };

    bgTask.setOnSucceeded(event -> {
      Map<String, List<String>> taskResult = bgTask.getValue();
      List<String> existingGenerations = taskResult.remove("generatedFiles");
      this.playbackFiles.addAll(existingGenerations);
      this.playbackSelect.setItems(this.playbackFiles);
      this.filesPerKey.set(FXCollections.observableMap(taskResult));
      this.keys = FXCollections.observableArrayList(this.filesPerKey.getValue().entrySet().stream()
          .map(es -> String.format("%s (%s songs)", es.getKey(), es.getValue().size()))
          .collect(Collectors.toList()));
      this.musicalKeySelect.setItems(FXCollections.observableList(this.keys));
      if (this.keys.isEmpty()) {
        this.infoLabel.setText("No input files found!");
      } else {
        this.musicalKeySelect.setValue(this.keys.get(0));
      }
    });

    bgTask.setOnFailed(event -> {
      this.infoLabel.setText("Failed to parse files");
    });

    bgTask.messageProperty().addListener((observable, oldValue, newValue) -> {
      this.infoLabel.setText(newValue);
    });

    bgTask.runningProperty().addListener((observable, oldValue, newValue) -> {
      this.isLoading.set(newValue);
    });

    Thread thread = new Thread(bgTask);
    thread.setDaemon(true);
    thread.start();
  }

}
