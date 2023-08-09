package org.juhanir.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import org.audiveris.proxymusic.Note;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.Step;
import org.juhanir.domain.MelodyNote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ScoreParserTest {

  @Nested
  class ConvertNoteToInt {

    private final ScoreParser parser = new ScoreParser();
    private final String[] notes = new String[] { "C", "D", "E", "F", "G", "A", "B" };

    @Test
    void invalidAlterValue() {
      assertThrows(IllegalArgumentException.class, () -> parser.convertNoteToInt("C", 3, -2));
      assertThrows(IllegalArgumentException.class, () -> parser.convertNoteToInt("C", 3, 2));
    }

    @Test
    void invalidOctave() {
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("C", Constants.OCTAVE_LOWER_BOUND - 1, 1));
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt("C", Constants.OCTAVE_UPPER_BOUND + 1, 1));
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
        int result = parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 0);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithFlatAlter() {
      int[] values = new int[] { -999, 1, 3, 4, 6, 8, 10 };
      for (int i = 1; i < notes.length; i++) { // no flat C
        int result = parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, -1);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithSharpAlter() {
      int[] values = new int[] { 1, 3, 5, 6, 8, 10, -999 };
      for (int i = 0; i < notes.length - 1; i++) { // no sharp B
        int result = parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 1);
        assertEquals(values[i], result);
      }
    }

    @Test
    void convertsNotesWithSupportedOctaves() {
      int[] values = new int[] { 0, 2, 4, 5, 7, 9, 11 };
      for (int i = 0; i < notes.length; i++) {
        assertEquals(values[i], parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND, 0));
        assertEquals(values[i] + 12,
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND + 1, 0));
        assertEquals(values[i] + 24,
            parser.convertNoteToInt(notes[i], Constants.OCTAVE_LOWER_BOUND + 2, 0));
      }
    }

    @Test
    void correctLowestAndHighestValues() {
      int lowest = parser.convertNoteToInt("C", Constants.OCTAVE_LOWER_BOUND, 0);
      int highest = parser.convertNoteToInt("B", Constants.OCTAVE_UPPER_BOUND, 0);
      assertEquals(0, lowest);
      assertEquals(Constants.NOTE_ARRAY_SIZE - 1, highest);
    }

  }

  @Nested
  class ConvertIntToNote {
    private final ScoreParser parser = new ScoreParser();
    private int[] alphabetSong = { 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 4, 4, 2, 9, 9, 7, 6, 6,
        6, 4, 9, 9, 7, 7, 6, 6, 4, 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 2 };

    @BeforeEach
    void setUp() {
      this.alphabetSong = Arrays.stream(this.alphabetSong).map(this::normalize).toArray();
    }

    int normalize(int note) {
      return note + (4 - Constants.OCTAVE_LOWER_BOUND) * 12;
    }

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
      MelodyNote[] notes = new MelodyNote[this.alphabetSong.length];
      for (int i = 0; i < this.alphabetSong.length; i++) {
        notes[i] = parser.convertIntToNote(this.alphabetSong[i], "D");
      }
      String[] expected = { "D", "D", "A", "A", "B", "B", "A", "G", "G", "F#", "F#", "E", "E", "E",
          "E", "D", "A", "A", "G", "F#", "F#", "F#", "E", "A", "A", "G", "G", "F#", "F#", "E", "D",
          "D", "A", "A", "B", "B", "A", "G", "G", "F#", "F#", "E", "E", "D" };
      assertArrayEquals(expected, Arrays.stream(notes).map(MelodyNote::toString).toArray());
      for (MelodyNote note : notes) {
        assertEquals(4, note.getOctave());
      }

    }

  }

  @Nested
  class ConvertMelodyToMusicXml {

    private final ScoreParser parser = new ScoreParser();
    private int[] alphabetSong = { 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 4, 4, 2, 9, 9, 7, 6, 6,
        6, 4, 9, 9, 7, 7, 6, 6, 4, 2, 2, 9, 9, 11, 11, 9, 7, 7, 6, 6, 4, 4, 2 };

    @BeforeEach
    void setUp() {
      this.alphabetSong = Arrays.stream(this.alphabetSong).map(this::normalize).toArray();
    }

    int normalize(int note) {
      return note + (4 - Constants.OCTAVE_LOWER_BOUND) * 12;
    }

    @Test
    void oneNoteInAllOctaves() {
      int numberOfOctaves = Constants.OCTAVE_UPPER_BOUND - Constants.OCTAVE_LOWER_BOUND + 1;
      int[] melody = new int[numberOfOctaves];
      for (int i = 0; i < numberOfOctaves; i++) {
        melody[i] = i * 12;
      }
      var result = parser.convertMelodyToScorePartwise(melody, "C");
      assertInstanceOf(ScorePartwise.class, result);
      Note[] notes = result.getPart().get(0).getMeasure().get(0).getNoteOrBackupOrForward().stream()
          .filter(Note.class::isInstance).map(c -> (Note) c).toArray(Note[]::new);
      for (int i = 0; i < notes.length; i++) {
        assertEquals(new BigDecimal(0), notes[i].getPitch().getAlter());
        assertEquals(Step.C, notes[i].getPitch().getStep());
        assertEquals(Constants.OCTAVE_LOWER_BOUND + i, notes[i].getPitch().getOctave());
        assertEquals(new BigDecimal(4), notes[i].getDuration());
        assertEquals("whole", notes[i].getType().getValue());
      }
    }

    @Test
    void convertsWholeTune() {
      ScorePartwise score = parser.convertMelodyToScorePartwise(alphabetSong, "D");
      Note[] notes = score.getPart().get(0).getMeasure().get(0).getNoteOrBackupOrForward().stream()
          .filter(Note.class::isInstance).map(c -> (Note) c).toArray(Note[]::new);
      Step[] steps = { Step.D, Step.D, Step.A, Step.A, Step.B, Step.B, Step.A, Step.G, Step.G,
          Step.F, Step.F, Step.E, Step.E, Step.E, Step.E, Step.D, Step.A, Step.A, Step.G, Step.F,
          Step.F, Step.F, Step.E, Step.A, Step.A, Step.G, Step.G, Step.F, Step.F, Step.E, Step.D,
          Step.D, Step.A, Step.A, Step.B, Step.B, Step.A, Step.G, Step.G, Step.F, Step.F, Step.E,
          Step.E, Step.D };
      BigDecimal[] alters =
            { new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(1), new BigDecimal(1), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(1),
              new BigDecimal(1), new BigDecimal(1), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(1),
              new BigDecimal(1), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0),
              new BigDecimal(0), new BigDecimal(0), new BigDecimal(0), new BigDecimal(1),
              new BigDecimal(1), new BigDecimal(0), new BigDecimal(0), new BigDecimal(0) };
      for (int i = 0; i < notes.length; i++) {
        assertEquals(alters[i], notes[i].getPitch().getAlter());
        assertEquals(steps[i], notes[i].getPitch().getStep());
        assertEquals(4, notes[i].getPitch().getOctave());
        assertEquals(new BigDecimal(4), notes[i].getDuration());
        assertEquals("whole", notes[i].getType().getValue());
      }
    }
  }

}
