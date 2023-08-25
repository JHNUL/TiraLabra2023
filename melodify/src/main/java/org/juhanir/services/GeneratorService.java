package org.juhanir.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.juhanir.Constants;
import org.juhanir.domain.Trie;
import org.juhanir.domain.TrieNode;

/**
 * Contains methods to generate a melody sequence.
 */
public class GeneratorService {

  private final Trie trie;
  private final Random random;
  private static final Logger generatorLogger = LogManager.getLogger();

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
   * @return index of the selected element or -1 if threshold was never passed
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
    TrieNode note = this.trie.lookup(prefix);
    double[] probabilities = this.trie.getProbabilities(note);
    return this.getIndexOfSelectedNote(probabilities);
  }

  /**
   * <p>
   * Predicts a melody sequence. Once minimum length is passed will
   * stop at the next note that is the same as first note of initial
   * prefix. Will produce a shorter sequence if a next note cannot be predicted.
   * If the initial note is not found after minimum length, will
   * produce at maximum minimumLength * 2 melody and then stop.
   * </p>
   *
   * @param initialPrefix starting notes of the sequence
   * @param minimumLength minimum length of the sequence
   * @return sequence of notes in integer representation
   */
  public int[] predictSequence(int[] initialPrefix, int minimumLength) {
    generatorLogger.info(
        String.format("Generating melody from common prefix %s", Arrays.toString(initialPrefix)));
    int[] result = Arrays.copyOf(initialPrefix, minimumLength * 2);
    int finalIndex = 0;
    for (int i = initialPrefix.length; i < result.length; i++) {
      int[] generationPrefix = Arrays.copyOfRange(result, i - initialPrefix.length, i);
      int nextNote = this.predictNextNote(generationPrefix);
      if (nextNote == -1) {
        generatorLogger.info(String.format("Could not continue generation of %s",
            Arrays.toString(generationPrefix)));
        return Arrays.copyOfRange(result, 0, i);
      }
      finalIndex = i;
      result[i] = nextNote;
      if (i >= (minimumLength - 1) && nextNote == initialPrefix[0]) {
        break;
      }
    }
    return Arrays.copyOfRange(result, 0, finalIndex + 1);
  }

  /**
   * <p>
   * Get the base note of the given key from the most popular octave in the training data.
   * </p>
   *
   * @param musicalKey name of the key, e.g. C, A#m etc
   * @return starting note integer value, -1 if no base note found for that key
   */
  public int getBaseNoteOfKey(String musicalKey) {
    int baseNote = Constants.musicalKeyBaseNotes.get(musicalKey);
    TrieNode root = trie.lookup(new int[0]); // empty prefix gets root
    TrieNode[] firstNotes = root.getChildren();
    int popularity = 0;
    int startingNote = -1;
    for (int i = baseNote; i < firstNotes.length; i += 12) {
      TrieNode note = firstNotes[i];
      if (note != null && note.getCount() > popularity) {
        startingNote = i;
        popularity = note.getCount();
      }
    }
    return startingNote;
  }

  /**
   * Transform the generated melody to playable Staccato string.
   *
   * @param melody       sequence of numerical notes
   * @param noteDuration note duration
   * @return Staccato string
   */
  public String toStaccatoString(int[] melody, String noteDuration) {
    String staccatoDuration;
    switch (noteDuration) {
      case "quarter":
        staccatoDuration = "q";
        break;
      case "eighth":
        staccatoDuration = "i";
        break;
      default:
        staccatoDuration = "s";
        break;
    }
    String[] staccatoMelody = Arrays
        .stream(melody)
        .map(note -> (Constants.OCTAVE_LOWER_BOUND * 12) + note)
        .mapToObj(note -> String.valueOf(note) + staccatoDuration)
        .toArray(String[]::new);
    return String.format("T%s %s", Constants.PLAYBACK_TEMPO, String.join(" ", staccatoMelody));
  }

  /**
   * Get names for generated files.
   *
   * @param key           musical key
   * @param degree        markov chain degree
   * @param noteDuration  note duration
   * @return array of two filenames, first staccato then midi
   */
  public String[] getGenerationFileNames(String key, int degree, String noteDuration) {
    String fileName = String.format("%s(%s)-degree%s-%s", key, noteDuration, degree,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd.HH.mm.ss.SSS")));
    return new String[] { String.format("%s.staccato", fileName), String.format("%s.MID", fileName) };
  }

}
