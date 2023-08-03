package org.juhanir.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.juhanir.domain.Trie;
import org.juhanir.domain.TrieNode;
import org.juhanir.utils.Constants;
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
    // Normalize melody to correct octave representation.
    // All notes in test data song are in octave 4
    this.wholeMelody =
        Arrays.stream(this.wholeMelody).map(this::normalize).toArray();
  }

  int normalize(int note) {
    return note + (4 - Constants.OCTAVE_LOWER_BOUND) * 12;
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

  void allSequencesAreInTrie(int degree) {
    Trie trie = new Trie();
    TrainingService service =
        new TrainingService(new FileIO(), new ScoreParser(), trie);
    service.trainWith(this.trainingDataPaths, degree);
    for (int i = 0; i < this.wholeMelody.length - degree; i++) {
      TrieNode last =
          trie.lookup(Arrays.copyOfRange(this.wholeMelody, i, i + degree + 1));
      assertNotNull(last);
      assertEquals(this.wholeMelody[i + degree], last.getValue());
    }
  }

  @Test
  void allNthDegreeSequencesAreInTrie() {
    for (int i = 1; i < 5; i++) {
      this.allSequencesAreInTrie(i);
    }
  }

}
