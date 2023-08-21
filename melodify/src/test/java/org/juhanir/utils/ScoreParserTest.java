package org.juhanir.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.juhanir.Constants;
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
    void nullStep() {
      assertThrows(IllegalArgumentException.class,
          () -> parser.convertNoteToInt(null, Constants.OCTAVE_LOWER_BOUND, 1));
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
  class WithRealData {

    private List<String> trainingDataPaths = new ArrayList<>();
    private String multiKeyFile;
    private String fifthsSmallFile;
    private String fifthsLargeFile;
    private String unsupportedModeFile;

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
      this.multiKeyFile = new File("src/test/resources/multi-keys.xml").getAbsolutePath();
      this.fifthsSmallFile = new File("src/test/resources/fifths-small.xml").getAbsolutePath();
      this.fifthsLargeFile = new File("src/test/resources/fifths-large.xml").getAbsolutePath();
      this.unsupportedModeFile = new File("src/test/resources/unsupported-mode.xml").getAbsolutePath();
    }

    @Test
    void groupsFilesPerMusicalKey() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      Map<String, List<String>> filesPerKey = parser.collectFilesPerKey(reader, this.trainingDataPaths);
      assertEquals(2, filesPerKey.size());
      assertTrue(filesPerKey.containsKey("G"));
      assertTrue(filesPerKey.containsKey("D"));
      assertEquals(1, filesPerKey.get("D").size());
      assertEquals(6, filesPerKey.get("G").size());
      assertTrue(filesPerKey.get("D").get(0).endsWith("alphabet-song.xml"));
    }

    @Test
    void doesNotThrowWithErrors() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      List<String> notPaths = List.of("/not/a/real/path");
      Map<String, List<String>> filesPerKey = parser.collectFilesPerKey(reader, notPaths);
      assertEquals(0, filesPerKey.size());
    }

    @Test
    void emptyFilePathList() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      List<String> notPaths = Lists.emptyList();
      Map<String, List<String>> filesPerKey = parser.collectFilesPerKey(reader, notPaths);
      assertEquals(0, filesPerKey.size());
    }

    @Test
    void getKeyForTuneResolvesKey() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      try (InputStream is = reader.readFile(this.trainingDataPaths.get(0))) {
        String key = parser.getKeyForTune(is);
        assertEquals("D", key);
      } catch (Exception e) {
        fail(e.getMessage());
      }
    }

    @Test
    void getKeyForTuneThrowsWithMultipleKeys() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      assertThrows(IllegalArgumentException.class,
          () -> parser.getKeyForTune(reader.readFile(this.multiKeyFile)));
    }

    @Test
    void throwsWhenFifthsValueTooSmall() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      assertThrows(IllegalArgumentException.class,
          () -> parser.getKeyForTune(reader.readFile(this.fifthsSmallFile)));
    }

    @Test
    void throwsWhenFifthsValueTooLarge() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      assertThrows(IllegalArgumentException.class,
          () -> parser.getKeyForTune(reader.readFile(this.fifthsLargeFile)));
    }

    @Test
    void throwsWhenModeNotSupported() {
      ScoreParser parser = new ScoreParser();
      FileIo reader = new FileIo();
      assertThrows(IllegalArgumentException.class,
          () -> parser.getKeyForTune(reader.readFile(this.unsupportedModeFile)));
    }

  }

}
