package org.juhanir.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project-wide constants.
 */
public class Constants {
  private Constants() {}

  public static final int FIFTHS_SUPPORTED_RANGE = 7;
  public static final int MARKOV_CHAIN_DEGREE_MIN = 1;
  public static final int MARKOV_CHAIN_DEGREE_MAX = 6;
  public static final int OCTAVE_LOWER_BOUND = 2;
  public static final int OCTAVE_UPPER_BOUND = 5;
  public static final int NOTE_ARRAY_SIZE = (OCTAVE_UPPER_BOUND - OCTAVE_LOWER_BOUND + 1) * 12;
  public static final String TRAINING_DATA_PATH = "data/musicxml";
  public static final String OUTPUT_DATA_PATH = "data/output";

  /**
   * <p>
   * Note names (without sharp/flat) as list where the index of the name specifies the note base
   * value as int (to be incremented with octave).
   * </p>
   * <p>
   * Where there is a whole step between notes the value is null.
   * </p>
   */
  public static final List<String> noteNames =
      Arrays.asList("C", null, "D", null, "E", "F", null, "G", null, "A", null, "B");

  /**
   * Modes that the application will accept from training data.
   */
  public static final List<String> modes = Arrays.asList("major", "minor", "dorian", "mixolydian");

  /**
   * <p>
   * Musical keys from the circle of fifths. In practice some of them are not supported by the
   * application but for completeness they're in the collection.
   * </p>
   * <p>
   * The value of the key is the integer representation of the base note of the key, e.g. for D it
   * is 2 since C=0 C#/Db=1 D=2. This corresponds to the position of the note in the *noteNames*
   * list.
   * </p>
   */
  public static final Map<String, Integer> musicalKeyBaseNotes = new HashMap<String, Integer>(30) {
    {
      put("C", 0);
      put("Cm", 0);
      put("C#", 1);
      put("C#m", 1);
      put("Db", 1);
      put("D", 2);
      put("Dm", 2);
      put("D#m", 3);
      put("Eb", 3);
      put("Ebm", 3);
      put("E", 4);
      put("Em", 4);
      put("F", 5);
      put("Fm", 5);
      put("F#", 6);
      put("F#m", 6);
      put("Gb", 6);
      put("G", 7);
      put("Gm", 7);
      put("G#m", 8);
      put("Ab", 8);
      put("Abm", 8);
      put("A", 9);
      put("Am", 9);
      put("A#m", 10);
      put("Bb", 10);
      put("Bbm", 10);
      put("B", 11);
      put("Bm", 11);
      put("Cb", 11);
    }
  };

}
