package org.juhanir.view;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.juhanir.domain.Trie;
import org.juhanir.utils.Constants;
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

  private ObservableList<String> keys =
      FXCollections.observableList(FXCollections.observableArrayList());
  private IntegerProperty degree = new SimpleIntegerProperty();
  private StringProperty musicalKey = new SimpleStringProperty();
  private MapProperty<String, List<String>> filesPerKey = new SimpleMapProperty<>();
  private AppEventHandler eventHandler;
  private final Trie trie;

  public AppController() {
    this.trie = new Trie();
    this.eventHandler = new AppEventHandler(trie, degree, musicalKey);
  }

  @FXML
  private void initialize() {
    this.eventHandler.handleDegreeFieldChange(this.degreeField);
    this.eventHandler.handleKeySelectChange(this.musicalKeySelect);
    this.eventHandler.handleTrainButtonClick(this.trainButton, this.filesPerKey);
    this.eventHandler.handleGenerateButtonClick(this.generateButton, this.filesPerKey);
    this.groupDataByKey();
  }

  private void groupDataByKey() {

    Task<ObservableMap<String, List<String>>> bgTask =
        new Task<ObservableMap<String, List<String>>>() {
          @Override
          protected ObservableMap<String, List<String>> call() throws Exception {
            updateMessage("Reading training data");
            FileIo reader = new FileIo();
            List<String> files = reader.getAllFilePathsInFolder(Constants.TRAINING_DATA_PATH);
            ScoreParser parser = new ScoreParser();
            updateMessage("");
            return FXCollections.observableMap(parser.collectFilesPerKey(reader, files));
          }
        };

    bgTask.setOnSucceeded(event -> {
      this.filesPerKey.set(bgTask.getValue());
      this.keys = FXCollections.observableArrayList(this.filesPerKey.getValue().entrySet().stream()
          .map(es -> String.format("%s (%s)", es.getKey(), es.getValue().size()))
          .collect(Collectors.toList()));
      this.musicalKeySelect.setItems(FXCollections.observableList(this.keys));
      this.musicalKeySelect.setValue(this.keys.get(0));
    });

    bgTask.messageProperty().addListener((observable, oldValue, newValue) -> {
      this.infoLabel.setText(newValue);
    });

    Thread thread = new Thread(bgTask);
    thread.setDaemon(true);
    thread.start();
  }

}
