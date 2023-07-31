package org.juhanir.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TrainingServiceTest {

  private List<String> trainingDataPaths = new ArrayList<>();
  private String wholeMelody = "DDAABBAGGFFEEEEDAAGFFFEAAGGFFEDDAABBAGGFFEED";

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

  // @Test
  // void allFirstDegreeSequencesAreInTrie() {
  //   Trie trie = new Trie();
  //   TrainingService service =
  //       new TrainingService(new FileIO(), new ScoreParser(), trie);
  //   service.trainWith(this.trainingDataPaths, 1);
  //   for (int i = 0; i < wholeMelody.length() - 1; i++) {
      
  //   }
  // }
}
