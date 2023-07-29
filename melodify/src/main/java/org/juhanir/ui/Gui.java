package org.juhanir.ui;

import java.util.List;

import org.juhanir.domain.Trie;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui extends Application {

    @Override
    public void start(Stage stage) {
        Trie trie = new Trie();
        TrainingService trainingService = new TrainingService(new FileIO(), new ScoreParser(), trie);
        Label label = new Label("Melodify");
        TextField textField = new TextField();
        textField.setText("/home/juhanir/Documents/TiraLabra2023/data/musicxml/alphabet-song.xml");
        TextField degreeField = new TextField();
        degreeField.setText("2");
        Button button = new Button("Parse file");
        button.setOnAction(event -> {
            trainingService.clear();
            trainingService.trainWith(List.of(textField.getText().strip()),
                    Integer.parseInt(degreeField.getText()));
        });
        Scene scene = new Scene(new VBox(label, button, textField, degreeField), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public void run() {
        launch();
    }

}
