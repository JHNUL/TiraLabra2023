package org.juhanir.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.juhanir.domain.Trie;
import org.juhanir.services.GeneratorService;
import org.juhanir.services.TrainingService;
import org.juhanir.utils.Constants;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;


/**
 * Event handlers for UI elements.
 */
public class AppEventHandler {

  private final Trie trie;
  private final IntegerProperty degree;
  private final StringProperty musicalKey;

  /**
   * Constructor.
   *
   * @param trie Trie data structure
   * @param degree Markov Chain degree
   * @param musicalKey Key that the user selected
   */
  public AppEventHandler(Trie trie, IntegerProperty degree, StringProperty musicalKey) {
    this.trie = trie;
    this.degree = degree;
    this.musicalKey = musicalKey;
  }

  /**
   * Event handler for degree textfield.
   *
   * @param degreeField UI element
   */
  public void handleDegreeFieldChange(TextField degreeField) {
    degreeField.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        int value = Integer.parseInt(newValue.strip());
        degree.set(value);
        degreeField.setStyle("-fx-border-color: none;");
      } catch (Exception e) {
        degree.set(0);
        degreeField.setStyle("-fx-border-color: red;");
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
    });
  }

  /**
   * Event handler for train button.
   *
   * @param trainButton UI element
   * @param filesPerKey Training data files grouped per musical key
   */
  public void handleTrainButtonClick(Button trainButton, Map<String, List<String>> filesPerKey) {
    trainButton.setOnAction(event -> {
      List<String> files = filesPerKey.getOrDefault(musicalKey.get(), Collections.emptyList());
      System.out.println("Files");
      System.out.println(files);
      if (files.isEmpty() || degree.get() < 1) {
        return;
      }
      try {
        FileIo reader = new FileIo();
        ScoreParser parser = new ScoreParser();
        TrainingService trainer = new TrainingService(reader, parser, this.trie);
        this.trie.clear();
        trainer.trainWith(files, degree.get());
      } catch (Exception e) {
        // TODO: report to the UI
        System.out.println(e.getMessage());
      }
    });
  }

  /**
   * Event handler for generate button.
   *
   * @param generateButton UI element
   * @param filesPerKey Training data files grouped per musical key
   */
  public void handleGenerateButtonClick(Button generateButton,
      Map<String, List<String>> filesPerKey) {
    generateButton.setOnAction(event -> {
      if (degree.get() < 0) {
        return;
      }
      int[] initialSequence = this.trie.getRandomSequence(degree.get());
      try {
        GeneratorService generator = new GeneratorService(trie, new Random());
        ScoreParser parser = new ScoreParser();
        int[] melody = generator.predictSequence(initialSequence, 50);
        String[] notes = Arrays.stream(melody)
            .mapToObj(note -> parser.convertIntToNote(note, musicalKey.get()).toString())
            .toArray(String[]::new);
        LocalDateTime now = LocalDateTime.now();
        String fileName = musicalKey.get() + "_generation_"
            + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss"));
        FileIo reader = new FileIo();
        reader.writeToFile(Constants.OUTPUT_DATA_PATH, fileName, String.join(" ", notes));
      } catch (Exception e) {
        // TODO: report to the UI
        System.out.println(e.getMessage());
      }
    });
  }

}
