package org.juhanir.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TrainingServiceTest {

  private List<String> trainingDataPaths = new ArrayList<>();
  private int[] wholeMelody =
      { 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 4, 4, 2, 9, 9, 7, 6, 6, 6, 4,
          9, 9, 7, 7, 6, 6, 4, 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 2 };

  @BeforeEach
  void setUp() {
    String path = "src/test/resources/alphabet-song.xml";
    File file = new File(path);
    String testDataPath = file.getAbsolutePath();
    if (!new File(testDataPath).exists()) {
      fail("Test data file not found " + path);
    }
    this.trainingDataPaths.add(testDataPath);

  }

  @Test
  void trieSizeIsCorrectForFirstDegree() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 1);
    assertEquals(21, trie.size());
  }

  @Test
  void trieSizeIsCorrectForSecondDegree() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 2);
    assertEquals(42, trie.size());
  }

  @Test
  void trieSizeIsCorrectForThirdDegree() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 3);
    assertEquals(70, trie.size());
  }

  @Test
  void allFirstDegreeSequencesAreInTrie() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 1);
    for (int i = 0; i < wholeMelody.length - 1; i++) {
      trie.lookup(Arrays.copyOfRange(wholeMelody, i, i + 2));
    }
  }

  @Test
  void allSecondDegreeSequencesAreInTrie() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 2);
    for (int i = 0; i < wholeMelody.length - 2; i++) {
      trie.lookup(Arrays.copyOfRange(wholeMelody, i, i + 3));
    }
  }

  @Test
  void allThirdDegreeSequencesAreInTrie() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 3);
    for (int i = 0; i < wholeMelody.length - 3; i++) {
      trie.lookup(Arrays.copyOfRange(wholeMelody, i, i + 4));
    }
  }

  @Test
  void allFourthDegreeSequencesAreInTrie() {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, 4);
    for (int i = 0; i < wholeMelody.length - 4; i++) {
      trie.lookup(Arrays.copyOfRange(wholeMelody, i, i + 5));
    }
  }
}
