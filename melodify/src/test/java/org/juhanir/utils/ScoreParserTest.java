package org.juhanir.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.juhanir.domain.MelodyNote;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ScoreParserTest {

  @Nested
  class ConvertNoteToInt {

    private final ScoreParser parser = new ScoreParser();
    private final String[] notes =
        new String[] { "C", "D", "E", "F", "G", "A", "B" };

    @Test
    void invalidAlterValue() {
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("C", 3, -2));
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("C", 3, 2));
    }

    @Test
    void invalidOctave() {
      assertThrows(IllegalArgumentException.class, () -> parser
          .convertNoteToInt("C", Constants.OCTAVE_LOWER_BOUND - 1, 1));
      assertThrows(IllegalArgumentException.class, () -> parser
          .convertNoteToInt("C", Constants.OCTAVE_UPPER_BOUND + 1, 1));
    }

    @Test
    void noFlatC() {
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("C", Constants.OCTAVE_LOWER_BOUND, -1));
    }

    @Test
    void noSharpB() {
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("B", Constants.OCTAVE_LOWER_BOUND, 1));
    }

    @Test
    void convertsNotesWithoutAlter() {
      int[] values = new int[] { 0, 2, 4, 5, 7, 9, 11 };
      for (int i = 0; i < notes.length; i++) {
        int result =
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 0);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithFlatAlter() {
      int[] values = new int[] { -999, 1, 3, 4, 6, 8, 10 };
      for (int i = 1; i < notes.length; i++) { // no flat C
        int result =
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, -1);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithSharpAlter() {
      int[] values = new int[] { 1, 3, 5, 6, 8, 10, -999 };
      for (int i = 0; i < notes.length - 1; i++) { // no sharp B
        int result =
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 1);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithSupportedOctaves() {
      int[] values = new int[] { 0, 2, 4, 5, 7, 9, 11 };
      for (int i = 0; i < notes.length; i++) {
        assertEquals(values[i],
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 0));
        assertEquals(values[i] + 12, parser.convertNoteToInt(notes[i],
            Constants.OCTAVE_LOWER_BOUND + 1, 0));
        assertEquals(values[i] + 24, parser.convertNoteToInt(notes[i],
            Constants.OCTAVE_LOWER_BOUND + 2, 0));
      }
    }

    @Test
    void correctLowestAndHighestValues() {
      int lowest =
          parser.convertNoteToInt("C", Constants.OCTAVE_LOWER_BOUND, 0);
      int highest =
          parser.convertNoteToInt("B", Constants.OCTAVE_UPPER_BOUND, 0);
      assertEquals(0, lowest);
      assertEquals(Constants.NOTE_ARRAY_SIZE - 1, highest);
    }

  }

  @Nested
  class ConvertIntToNote {
    private final ScoreParser parser = new ScoreParser();
    private final int[] alphabetSong = { 14, 14, 21, 21, 23, 23, 21, 19, 19, 18,
        18, 16, 16, 16, 16, 14, 21, 21, 19, 18, 18, 18, 16, 21, 21, 19, 19, 18,
        18, 16, 14, 14, 21, 21, 23, 23, 21, 19, 19, 18, 18, 16, 16, 14 };

    @Test
    void resolvesOctaveCorrectly() {
      MelodyNote note = parser.convertIntToNote(0, "C");
      assertEquals(Constants.OCTAVE_LOWER_BOUND, note.getOctave());
      note = parser.convertIntToNote(12, "C");
      assertEquals(Constants.OCTAVE_LOWER_BOUND + 1, note.getOctave());
      note = parser.convertIntToNote(24, "C");
      assertEquals(Constants.OCTAVE_LOWER_BOUND + 2, note.getOctave());
    }

    @Test
    void resolvesAlterCorrectlySharp() {
      MelodyNote note = parser.convertIntToNote(1, "D");
      assertEquals(1, note.getAlter());
      note = parser.convertIntToNote(3, "D");
      assertEquals(1, note.getAlter());
      note = parser.convertIntToNote(6, "G");
      assertEquals(1, note.getAlter());
    }

    @Test
    void resolvesAlterCorrectlyFlat() {
      MelodyNote note = parser.convertIntToNote(1, "F");
      assertEquals(-1, note.getAlter());
      note = parser.convertIntToNote(3, "F");
      assertEquals(-1, note.getAlter());
      note = parser.convertIntToNote(10, "Bb");
      assertEquals(-1, note.getAlter());
    }

    @Test
    void resolvesAlterCorrectlyNatural() {
      MelodyNote note = parser.convertIntToNote(0, "C");
      assertEquals(0, note.getAlter());
      note = parser.convertIntToNote(7, "F");
      assertEquals(0, note.getAlter());
      note = parser.convertIntToNote(0, "Bb");
      assertEquals(0, note.getAlter());
    }

    @Test
    void resolvesNotes() {
      MelodyNote[] notes = new MelodyNote[alphabetSong.length];
      for (int i = 0; i < alphabetSong.length; i++) {
        notes[i] = parser.convertIntToNote(alphabetSong[i], "D");
      }
      String[] expected = { "D", "D", "A", "A", "B", "B", "A", "G", "G", "F#",
          "F#", "E", "E", "E", "E", "D", "A", "A", "G", "F#", "F#", "F#", "E",
          "A", "A", "G", "G", "F#", "F#", "E", "D", "D", "A", "A", "B", "B",
          "A", "G", "G", "F#", "F#", "E", "E", "D" };
      assertArrayEquals(expected,
          Arrays.stream(notes).map(MelodyNote::toString).toArray());
      for (MelodyNote note : notes) {
        assertEquals(4, note.getOctave());
      }

    }

  }

}
