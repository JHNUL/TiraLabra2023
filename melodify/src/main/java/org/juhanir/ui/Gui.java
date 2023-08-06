package org.juhanir.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.juhanir.domain.Trie;
import org.juhanir.services.GeneratorService;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.Constants;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;

/**
 * Main application class.
 */
public class Gui extends Application {

  private int getDegree(Label errorLabel, TextField degreeField) {
    int degree = 0;
    try {
      degree = Integer.parseInt(degreeField.getText());
      if (degree < Constants.MARKOV_CHAIN_DEGREE_MIN
          || degree > Constants.MARKOV_CHAIN_DEGREE_MAX) {
        throw new IllegalArgumentException();
      }
      return degree;
    } catch (Exception e) {
      errorLabel.setText("Invalid degree value!");
      errorLabel.setVisible(true);
      return -1;
    }
  }

  @Override
  public void start(Stage stage) {

    Label errorLabel = new Label();
    errorLabel.setTextFill(Color.RED);
    errorLabel.setVisible(false);

    FileIo reader = new FileIo();
    List<String> files =
        reader.getAllFilePathsInFolder(Constants.TRAINING_DATA_PATH);
    if (files.isEmpty()) {
      errorLabel.setText("No training data files detected!");
      errorLabel.setVisible(true);
    }
    ScoreParser parser = new ScoreParser();
    Map<String, List<String>> filesPerKey =
        parser.collectFilesPerKey(reader, files);

    ObservableList<String> musicalKeySelectionValues =
        FXCollections.observableArrayList();
    List<String> musicalKeyOpts = filesPerKey.entrySet().stream()
        .map(es -> String.format("%s (%s)", es.getKey(), es.getValue().size()))
        .collect(Collectors.toList());
    musicalKeySelectionValues.addAll(musicalKeyOpts);

    Label keyLabel = new Label("Select key");
    ComboBox<String> musicalKeySelect =
        new ComboBox<>(musicalKeySelectionValues);
    musicalKeySelect.setValue(musicalKeySelectionValues.get(0));

    Label degreeLabel = new Label("Select Markov Chain degree");
    TextField degreeField = new TextField();
    degreeField.setText("2");

    Button generateButton = new Button("Generate");
    generateButton.setVisible(false);
    Button trainButton = new Button("Train");
    Trie trie = new Trie();
    trainButton.setOnAction(event -> {
      generateButton.setVisible(false);
      if (musicalKeySelect.getValue() == null
          || musicalKeySelect.getValue().isBlank()) {
        errorLabel.setText("No key selected!");
        errorLabel.setVisible(true);
        return;
      }
      int degree = this.getDegree(errorLabel, degreeField);
      if (degree < 0) {
        return;
      }
      errorLabel.setVisible(false);
      try {
        TrainingService trainer = new TrainingService(reader, parser, trie);
        trie.clear();
        String selectedKey = musicalKeySelect.getValue().split(" ")[0].strip();
        trainer.trainWith(filesPerKey.get(selectedKey), degree);
        generateButton.setVisible(true);
      } catch (Exception e) {
        errorLabel.setText(String.format("Fatal error: %s", e.getMessage()));
        errorLabel.setVisible(true);
      }
    });

    generateButton.setOnAction(event -> {
      int degree = this.getDegree(errorLabel, degreeField);
      if (degree < 0) {
        return;
      }
      int[] initialSequence = trie.getRandomSequence(degree);
      try {
        GeneratorService generator = new GeneratorService(trie, new Random());
        int[] melody = generator.predictSequence(initialSequence, 50);
        System.out.println(Arrays.toString(melody));
        String selectedKey = musicalKeySelect.getValue().split(" ")[0].strip();
        String[] notes = Arrays.stream(melody)
            .mapToObj(
                note -> parser.convertIntToNote(note, selectedKey).toString())
            .toArray(String[]::new);
        LocalDateTime now = LocalDateTime.now();
        String fileName = selectedKey + "_generation_"
            + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss"));
        reader.writeToFile(Constants.OUTPUT_DATA_PATH, fileName,
            String.join(" ", notes));
      } catch (Exception e) {
        errorLabel.setText(String.format("Fatal error: %s", e.getMessage()));
        errorLabel.setVisible(true);
      }
    });

    Scene scene = new Scene(new VBox(keyLabel, musicalKeySelect, degreeLabel,
        degreeField, trainButton, generateButton, errorLabel), 640, 480);
    stage.setTitle("MELODIFY");
    stage.setScene(scene);
    stage.show();
  }

  public void run() {
    launch();
  }

}
