package org.juhanir.utils;

public class Constants {
    private Constants() {
    }

    public static final int FIFTHS_SUPPORTED_RANGE = 6;
    public static final int OCTAVE_LOWER_BOUND = 3;
    public static final int OCTAVE_UPPER_BOUND = 5;
    public static final int NOTE_ARRAY_SIZE = (OCTAVE_UPPER_BOUND - OCTAVE_LOWER_BOUND + 1) * 12;
    public static final String TRAINING_DATA_PATH = "data/musicxml";

}
