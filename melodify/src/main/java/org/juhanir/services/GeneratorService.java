package org.juhanir.services;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import org.juhanir.domain.Trie;
import org.juhanir.domain.TrieNode;

/**
 * Contains methods to generate a melody sequence.
 */
public class GeneratorService {

  private final Trie trie;
  private final Random random;
  private static Logger generatorLogger = Logger.getLogger(GeneratorService.class.getName());

  public GeneratorService(Trie trie, Random rand) {
    this.trie = trie;
    this.random = rand;
  }

  /**
   * <p>
   * Calculates the point in the array of probabilities where the cumulative probability passes the
   * threshold.
   * </p>
   * <p>
   * Caller must make sure the probabilities add up to exactly 1.
   * </p>
   *
   * @param probabilities array of probabilities
   * @return index of the selected element or -1 if threshold was never passed (should not happen if
   *         called with legal argument)
   */
  public int getIndexOfSelectedNote(double[] probabilities) {
    double threshold = this.random.nextDouble();
    for (int i = 0; i < probabilities.length; i++) {
      double current = probabilities[i];
      if (current > threshold) {
        return i;
      }
      threshold -= current;
    }
    return -1;
  }

  /**
   * <p>
   * Predicts the next note of a prefix sequence. The prediction happens according to the
   * probability distribution of the child notes of the last note in the input sequence.
   * </p>
   *
   * @param prefix sequence whose next note we are predicting
   * @return value of the note, -1 if no children
   * @throws IllegalArgumentException if prefix has children whose probabilities do not add up to
   *         one.
   */
  public int predictNextNote(int[] prefix) {
    TrieNode[] children = this.trie.prefixSearch(prefix);
    double[] probabilities = this.trie.getProbabilities(children);
    // Use threshold for comparison because of floating point precision
    double sum = Arrays.stream(probabilities).sum();
    double epsilon = 1e-10;
    if (sum == 0.0) { // this is a valid case for no children
      return -1;
    } else if (Math.abs(1.0 - sum) > epsilon) {
      throw new IllegalArgumentException(
          "Probabilities must sum up to one " + Arrays.toString(probabilities));
    }
    return this.getIndexOfSelectedNote(probabilities);
  }

  /**
   * <p>
   * Predict a melody sequence of input length. Will produce a shorter sequence if a next note
   * cannot be predicted.
   * </p>
   *
   * @param initialPrefix starting notes of the sequence
   * @param length maximum length of the sequence
   * @return sequence of notes in integer representation
   */
  public int[] predictSequence(int[] initialPrefix, int length) {
    int[] result = Arrays.copyOf(initialPrefix, length);
    for (int i = initialPrefix.length; i < result.length; i++) {
      int[] generationPrefix = Arrays.copyOfRange(result, i - initialPrefix.length, i);
      int nextNote = this.predictNextNote(generationPrefix);
      if (nextNote < 0) {
        return Arrays.copyOfRange(result, 0, i);
      }
      result[i] = nextNote;
    }
    return result;
  }
}