package org.juhanir.services;

import java.util.Arrays;
import java.util.Random;
import org.juhanir.domain.Trie;
import org.juhanir.domain.TrieNode;

/**
 * Contains methods to generate a melody sequence.
 */
public class GeneratorService {

  private final Trie trie;
  private final Random random;

  public GeneratorService(Trie trie, Random rand) {
    this.trie = trie;
    this.random = rand;
  }

  /**
   * <p>
   * Calculates the point in the array of probabilities where the cumulative
   * probability passes the threshold.
   * </p>
   * <p>
   * Caller must make sure the probabilities add up to exactly 1.
   * </p>
   *
   * @param probabilities array of probabilities
   * @return index of the selected element or -1 if threshold was never passed
   *         (should not happen if called with legal argument)
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
   * Predicts the next note of a prefix sequence. The prediction happens
   * according to the probability distribution of the child notes of the last
   * note in the input sequence.
   * </p>
   *
   * @param prefix sequence whose next note we are predicting
   * @return value of the note, -1 if no children
   */
  public int predictNextNote(int[] prefix) {
    TrieNode[] children = this.trie.prefixSearch(prefix);
    double[] probabilities = this.trie.getProbabilities(children);
    double sum = Arrays.stream(probabilities).sum();
    if (sum == 0.0) { // valid case, since leaves have a child array with zeroes
      return -1;
    } else if (sum != 1.0) {
      throw new IllegalArgumentException("Probabilities must sum up to one");
    }
    return this.getIndexOfSelectedNote(probabilities);
  }
}
