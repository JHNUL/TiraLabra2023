package org.juhanir.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.juhanir.Constants;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GeneratorServiceTest {

  private Trie trie;

  @BeforeEach
  void setUp() {
    final int[] firstBranch = { 5, 6, 7 };
    final int[] secondBranch = { 5, 6, 8 };
    final int[] thirdBranch = { 5, 6, 9 };
    final int[] fourthBranch = { 5, 6, 10 };
    final int[] fifthBranch = { 5, 7, 11 };
    this.trie = new Trie();
    this.trie.insert(firstBranch);
    this.trie.insert(secondBranch);
    this.trie.insert(thirdBranch);
    this.trie.insert(fourthBranch);
    this.trie.insert(fifthBranch);
  }

  @Test
  void predictionThrowsWhenProbabilitySumNotEqualToOne() {
    Trie mockTrie = mock(Trie.class);
    GeneratorService generator = new GeneratorService(mockTrie, new Random());
    when(mockTrie.getProbabilities(any())).thenReturn(new double[] { 0.0, 0.2, 0.5 })
        .thenReturn(new double[] { 0.0, 0.8, 0.5 });
    assertThrows(IllegalArgumentException.class,
        () -> generator.predictNextNote(new int[] { 5, 6 }));
    assertThrows(IllegalArgumentException.class,
        () -> generator.predictNextNote(new int[] { 5, 6 }));
  }

  @Test
  void predictionWithNonExistingPrefix() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    List<int[]> inputs = List.of(new int[] { 1, 1, 1 }, new int[] { 7, 11 }, new int[] { 6, 7 },
        new int[] { 5, 8 }, new int[] { 6, 5 });
    for (int[] input : inputs) {
      int result = generator.predictNextNote(input);
      assertEquals(-1, result);
    }
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void predictionWithExistingPrefixAndNoChildren() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    int result = generator.predictNextNote(new int[] { 5, 6, 7 });
    assertEquals(-1, result);
  }

  @Test
  void predictionFromRoot() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    int result = generator.predictNextNote(new int[] {});
    assertEquals(5, result);
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void predictionWithValidPrefix1() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    List<Integer> expectedValues = List.of(6, 7);
    for (int i = 0; i < 100; i++) {
      int result = generator.predictNextNote(new int[] { 5 });
      assertTrue(expectedValues.contains(result));
    }
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void predictionWithValidPrefix2() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    for (int i = 0; i < 100; i++) {
      int result = generator.predictNextNote(new int[] { 5, 7 });
      assertEquals(11, result);
    }
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void predictionWithValidPrefix3() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    List<Integer> expectedValues = List.of(7, 8, 9, 10);
    for (int i = 0; i < 100; i++) {
      int result = generator.predictNextNote(new int[] { 5, 6 });
      assertTrue(expectedValues.contains(result));
    }
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void noBaseNoteOfKey() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    int res = generator.getBaseNoteOfKey("C");
    assertEquals(-1, res);
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void findsBaseNoteOfKey() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    int res = generator.getBaseNoteOfKey("F");
    assertEquals(5, res);
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
  }

  @Test
  void findsBaseNoteOfKeys() {
    Trie testTrie = new Trie();
    for (int i = 0; i < Constants.NOTE_ARRAY_SIZE; i++) {
      testTrie.insert(new int[] { i, 6 });
      // add all notes twice in the first octave
      if (i < 12) {
        testTrie.insert(new int[] { i, 6 });
      }
      // add every other note three times in the last octave
      if (i >= Constants.NOTE_ARRAY_SIZE - 12 && i % 2 == 0) {
        testTrie.insert(new int[] { i, 6 });
        testTrie.insert(new int[] { i, 6 });
      }
    }
    GeneratorService generator = new GeneratorService(testTrie, new Random());
    // Expected in the lowest octave
    assertEquals(1, generator.getBaseNoteOfKey("C#"));
    assertEquals(1, generator.getBaseNoteOfKey("C#m"));
    assertEquals(1, generator.getBaseNoteOfKey("Db"));
    assertEquals(3, generator.getBaseNoteOfKey("D#m"));
    assertEquals(3, generator.getBaseNoteOfKey("Eb"));
    assertEquals(3, generator.getBaseNoteOfKey("Ebm"));
    assertEquals(5, generator.getBaseNoteOfKey("F"));
    assertEquals(5, generator.getBaseNoteOfKey("Fm"));
    assertEquals(7, generator.getBaseNoteOfKey("G"));
    assertEquals(7, generator.getBaseNoteOfKey("Gm"));
    assertEquals(9, generator.getBaseNoteOfKey("A"));
    assertEquals(9, generator.getBaseNoteOfKey("Am"));
    assertEquals(11, generator.getBaseNoteOfKey("B"));
    assertEquals(11, generator.getBaseNoteOfKey("Bm"));
    assertEquals(11, generator.getBaseNoteOfKey("Cb"));
    // Expected in the highest octave
    assertEquals(Constants.NOTE_ARRAY_SIZE - 12, generator.getBaseNoteOfKey("C"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 12, generator.getBaseNoteOfKey("Cm"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 10, generator.getBaseNoteOfKey("D"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 10, generator.getBaseNoteOfKey("Dm"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 8, generator.getBaseNoteOfKey("E"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 8, generator.getBaseNoteOfKey("Em"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 6, generator.getBaseNoteOfKey("F#"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 6, generator.getBaseNoteOfKey("F#m"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 6, generator.getBaseNoteOfKey("Gb"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 4, generator.getBaseNoteOfKey("G#m"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 4, generator.getBaseNoteOfKey("Ab"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 4, generator.getBaseNoteOfKey("Abm"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 2, generator.getBaseNoteOfKey("A#m"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 2, generator.getBaseNoteOfKey("Bb"));
    assertEquals(Constants.NOTE_ARRAY_SIZE - 2, generator.getBaseNoteOfKey("Bbm"));
  }

  @Nested
  class WeightedProbabilitySelection {

    private GeneratorService generator;
    private GeneratorService generatorWithMockRandom;
    private Random mockRandom;

    @BeforeEach
    void setUp() {
      Trie trie = new Trie();
      this.mockRandom = mock(Random.class);
      this.generator = new GeneratorService(trie, new Random());
      this.generatorWithMockRandom = new GeneratorService(trie, mockRandom);
    }

    @AfterEach
    void cleanUp() {
      reset(mockRandom);
    }

    @Test
    void handlesEmptyList() {
      int result = this.generator.getIndexOfSelectedNote(new double[] {});
      assertEquals(-1, result);
    }

    @Test
    void returnsCorrectIndexWithMockedRandom() {
      when(mockRandom.nextDouble())
          .thenReturn(0.999)
          .thenReturn(0.1)
          .thenReturn(0.2)
          .thenReturn(0.4)
          .thenReturn(0.4321324)
          .thenReturn(0.0);
      double[] probs = { 0.0, 0.0, 0.1, 0.32, 0.08, 0.4, 0.2, 0.0 };
      assertEquals(0.2,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.08,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.1,
          probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
    }

    @Test
    void returnsIndexOfNonZeroValue() {
      double[] testProbabilities = { 0.05, 0.0, 0.1, 0.0, 0.32, 0.08, 0.0, 0.35, 0.2, 0.0 };
      List<Double> expectedValues = List.of(0.1, 0.32, 0.08, 0.35, 0.2, 0.05);
      for (int i = 0; i < 100; i++) {
        int res = this.generator.getIndexOfSelectedNote(testProbabilities);
        assertTrue(expectedValues.contains(testProbabilities[res]));
      }
    }

    @Test
    void moreProbableNoteGetsPickedSanityTest() {
      // Bad idea to test with randomness, but hopefully after ten thousand
      // iterations we see that 0.2 is always picked less often than 0.35
      double[] testProbabilities = { 0.05, 0.0, 0.1, 0.0, 0.32, 0.08, 0.0, 0.35, 0.2, 0.0 };
      int countOfMostProbable = 0;
      int countOfLessProbable = 0;
      for (int i = 0; i < 10000; i++) {
        int res = this.generator.getIndexOfSelectedNote(testProbabilities);
        if (testProbabilities[res] == 0.35) {
          countOfMostProbable++;
        }
        if (testProbabilities[res] == 0.2) {
          countOfLessProbable++;
        }
      }
      assertTrue(countOfMostProbable > countOfLessProbable);
    }
  }

  @Nested
  class WithRealData {

    private List<String> trainingDataPaths = new ArrayList<>();
    private int[] wholeMelody = { 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 4, 4, 2, 9, 9, 7, 6, 6,
        6, 4, 9, 9, 7, 7, 6, 6, 4, 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 2 };

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
      this.wholeMelody = Arrays.stream(this.wholeMelody).map(this::normalize).toArray();
    }

    int normalize(int note) {
      return note + (4 - Constants.OCTAVE_LOWER_BOUND) * 12;
    }

    boolean arrayHasSubArray(int[] array, int[] subArray) {
      for (int i = 0; i < array.length - subArray.length + 1; i++) {
        if (Arrays.equals(array, i, i + subArray.length, subArray, 0, subArray.length)) {
          return true;
        }
      }
      return false;
    }

    void allGenerationsAreInTrainingData(int degree) {
      Trie trie = new Trie();
      TrainingService service = new TrainingService(new FileIo(), new ScoreParser(), trie);
      service.trainWith(this.trainingDataPaths, degree);
      GeneratorService generator = new GeneratorService(trie, new Random());
      for (int i = 0; i < this.wholeMelody.length - degree; i++) {
        int[] prefix = Arrays.copyOfRange(this.wholeMelody, i, i + degree);
        int nextNote = generator.predictNextNote(prefix);
        int[] generation = Arrays.copyOf(prefix, prefix.length + 1);
        generation[generation.length - 1] = nextNote;
        assertTrue(this.arrayHasSubArray(this.wholeMelody, generation));
      }
    }

    @Test
    void allNthDegreeGenerationsAreInTrainingData() {
      for (int i = 1; i < 4; i++) {
        this.allGenerationsAreInTrainingData(i);
      }
    }

    void allPredictedSequencesAreInTrainingData(int degree) {
      Trie trie = new Trie();
      TrainingService service = new TrainingService(new FileIo(), new ScoreParser(), trie);
      service.trainWith(this.trainingDataPaths, degree);
      GeneratorService generator = new GeneratorService(trie, new Random());
      int[] prefix = Arrays.copyOfRange(this.wholeMelody, 0, degree);
      int[] generation = generator.predictSequence(prefix, this.wholeMelody.length);
      assertEquals(this.wholeMelody.length, generation.length);
      assertArrayEquals(prefix, Arrays.copyOfRange(generation, 0, degree));
      for (int i = 0; i < generation.length - degree; i++) {
        int[] subArray = Arrays.copyOfRange(generation, i, i + degree + 1);
        assertTrue(this.arrayHasSubArray(this.wholeMelody, subArray));
      }
    }

    @Test
    void predictSequenceResultsAreInTrainingData() {
      // up to 3rd degree all sequences are in the test data,
      // 4th degree has an ending sequence that has no children
      for (int i = 1; i < 4; i++) {
        this.allPredictedSequencesAreInTrainingData(i);
      }
    }

    @Test
    void predictSequenceStopsIfNoChildrenForSequence() {
      Trie trie = new Trie();
      TrainingService service = new TrainingService(new FileIo(), new ScoreParser(), trie);
      service.trainWith(this.trainingDataPaths, 4);
      GeneratorService generator = new GeneratorService(trie, new Random());
      // From the end of training data, non-repeating sequence
      int[] prefix = Arrays.copyOfRange(this.wholeMelody, this.wholeMelody.length - 5,
          this.wholeMelody.length);
      int[] generation = generator.predictSequence(prefix, this.wholeMelody.length);
      assertArrayEquals(prefix, generation);
    }

  }

  @Nested
  class WithMoreRealData {

    private List<String> trainingDataPaths = new ArrayList<>();

    @BeforeEach
    void setUp() {
      String[] files = { "alphabet-song.xml", "a07.xml", "a34.xml", "a83.xml", "g99.xml", "g112.xml", "g118.xml" };
      for (String file : files) {
        File sourceFile = new File("src/test/resources/" + file);
        String testDataPath = sourceFile.getAbsolutePath();
        if (!new File(testDataPath).exists()) {
          fail("Test data file not found " + testDataPath);
        } else {
          this.trainingDataPaths.add(testDataPath);
        }
      }
    }

    int normalize(int note) {
      return note + (4 - Constants.OCTAVE_LOWER_BOUND) * 12;
    }

    boolean arrayHasSubArray(int[] array, int[] subArray) {
      for (int i = 0; i < array.length - subArray.length + 1; i++) {
        if (Arrays.equals(array, i, i + subArray.length, subArray, 0, subArray.length)) {
          return true;
        }
      }
      return false;
    }

    boolean anyListContainsSequence(List<List<Integer>> lists, int[] sequence) {
      for (List<Integer> list : lists) {
        int[] melody = list.stream().mapToInt(Integer::intValue).toArray();
        if (this.arrayHasSubArray(melody, sequence)) {
          return true;
        }
      }
      return false;
    }

    void allGenerationsAreInTrainingData(int degree) {
      Trie trie = new Trie();
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      List<List<Integer>> sourceMelodies = new ArrayList<>();
      for (String filePath : trainingDataPaths) {
        try (InputStream is = reader.readFile(filePath)) {
          List<Integer> sourceMelody = parser.parse(is);
          sourceMelodies.add(sourceMelody);
        } catch (Exception e) {
          fail("Failed to parse source file " + filePath);
        }
      }
      TrainingService service = new TrainingService(reader, parser, trie);
      service.trainWith(this.trainingDataPaths, degree);
      GeneratorService generator = new GeneratorService(trie, new Random());
      int[] generation = generator.predictSequence(trie.getRandomSequence(degree), Constants.GENERATED_MELODY_LEN);
      for (int i = 0; i < generation.length - degree; i++) {
        int[] sequence = Arrays.copyOfRange(generation, i, i + degree);
        assertTrue(this.anyListContainsSequence(sourceMelodies, sequence));
      }
    }

    @Test
    void allNthDegreeGenerationsAreInTrainingData() {
      for (int i = Constants.MARKOV_CHAIN_DEGREE_MIN; i <= Constants.MARKOV_CHAIN_DEGREE_MAX; i++) {
        this.allGenerationsAreInTrainingData(i);
      }
    }

  }
}
