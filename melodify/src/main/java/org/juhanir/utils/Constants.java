package org.juhanir.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Project-wide constants.
 */
public class Constants {
  private Constants() {}

  public static final int FIFTHS_SUPPORTED_RANGE = 7;
  public static final int OCTAVE_LOWER_BOUND = 3;
  public static final int OCTAVE_UPPER_BOUND = 5;
  public static final int NOTE_ARRAY_SIZE =
      (OCTAVE_UPPER_BOUND - OCTAVE_LOWER_BOUND + 1) * 12;
  public static final String TRAINING_DATA_PATH = "data/musicxml";

  /**
   * <p>
   * Note names (without sharp/flat) as list where the index of the name
   * specifies the note base value as int (to be incremented with octave).
   * </p>
   * <p>
   * Where there is a whole step between notes the value is null.
   * </p>
   */
  public static final List<String> noteNames = Arrays.asList("C", null, "D",
      null, "E", "F", null, "G", null, "A", null, "B");

}
