package org.juhanir.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;
import org.juhanir.domain.Trie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GeneratorServiceTest {

  private Trie trie;

  @BeforeEach
  void setUp() {
    final int[] firstBranch = {5, 6, 7};
    final int[] secondBranch = {5, 6, 8};
    final int[] thirdBranch = {5, 6, 9};
    final int[] fourthBranch = {5, 6, 10};
    final int[] fifthBranch = {5, 7, 11};
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
    when(mockTrie.getProbabilities(any())).thenReturn(new double[] {0.0, 0.2, 0.5})
        .thenReturn(new double[] {0.0, 0.8, 0.5});
    assertThrows(IllegalArgumentException.class, () -> generator.predictNextNote(new int[] {5, 6}));
    assertThrows(IllegalArgumentException.class, () -> generator.predictNextNote(new int[] {5, 6}));
  }

  @Test
  void predictionWithNonExistingPrefix() {
    GeneratorService generator = new GeneratorService(this.trie, new Random());
    List<int[]> inputs = List.of(new int[] {1, 1, 1}, new int[] {7, 11}, new int[] {6, 7},
        new int[] {5, 8}, new int[] {6, 5});
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
    int result = generator.predictNextNote(new int[] {5, 6, 7});
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
      int result = generator.predictNextNote(new int[] {5});
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
      int result = generator.predictNextNote(new int[] {5, 7});
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
      int result = generator.predictNextNote(new int[] {5, 6});
      assertTrue(expectedValues.contains(result));
    }
    // {root}
    // {5}
    // {6}___________{7}
    // {7}{8}{9}{10} {11}
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
      assertEquals(0.2, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.32, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.08, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
      assertEquals(0.1, probs[this.generatorWithMockRandom.getIndexOfSelectedNote(probs)]);
    }

    @Test
    void returnsIndexOfNonZeroValue() {
      double[] testProbabilities = {0.05, 0.0, 0.1, 0.0, 0.32, 0.08, 0.0, 0.35, 0.2, 0.0};
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
      double[] testProbabilities = {0.05, 0.0, 0.1, 0.0, 0.32, 0.08, 0.0, 0.35, 0.2, 0.0};
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

}
