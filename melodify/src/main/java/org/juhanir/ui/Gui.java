package org.juhanir.ui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.juhanir.domain.Trie;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.Constants;
import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;

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

public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        FileIO reader = new FileIO();
        ScoreParser parser = new ScoreParser();
        Trie trie = new Trie();
        TrainingService trainingService = new TrainingService(reader, parser, trie);
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);

        List<String> files = reader.getAllFilePathsInFolder(Constants.TRAINING_DATA_PATH);
        if (files.isEmpty()) {
            errorLabel.setText("No training data files detected!");
            errorLabel.setVisible(true);
        }
        Map<String, List<String>> filesPerKey = parser.collectFilesPerKey(reader, files);

        ObservableList<String> musicalKeySelectionValues = FXCollections.observableArrayList();
        List<String> musicalKeyOpts = filesPerKey.entrySet().stream()
                .map(es -> String.format("%s (%s)", es.getKey(), es.getValue().size()))
                .collect(Collectors.toList());
        musicalKeySelectionValues.addAll(musicalKeyOpts);
        Label keyLabel = new Label("Select key");
        ComboBox<String> musicalKeySelect = new ComboBox<>(musicalKeySelectionValues);
        Label degreeLabel = new Label("Select Markov Chain degree");
        TextField degreeField = new TextField();
        degreeField.setText("2");
        Button button = new Button("Train");
        button.setOnAction(event -> {
            if (musicalKeySelect.getValue() == null || musicalKeySelect.getValue().isBlank()) {
                errorLabel.setText("No key selected!");
                errorLabel.setVisible(true);
                return;
            }
            int degree = 0;
            try {
                degree = Integer.parseInt(degreeField.getText());
                if (degree < 1) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                errorLabel.setText("Invalid degree value!");
                errorLabel.setVisible(true);
                return;
            }
            errorLabel.setVisible(false);
            trainingService.clear();
            String selectedKey = musicalKeySelect.getValue().split(" ")[0].strip();
            trainingService.trainWith(filesPerKey.get(selectedKey), degree);
        });
        Scene scene = new Scene(new VBox(keyLabel, musicalKeySelect, degreeLabel, degreeField, button, errorLabel), 640,
                480);
        stage.setTitle("MELODIFY");
        stage.setScene(scene);
        stage.show();
    }

    public void run() {
        launch();
    }

}
