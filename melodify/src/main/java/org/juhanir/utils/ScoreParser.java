package org.juhanir.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.audiveris.proxymusic.Attributes;
import org.audiveris.proxymusic.Key;
import org.audiveris.proxymusic.Note;
import org.audiveris.proxymusic.NoteType;
import org.audiveris.proxymusic.ObjectFactory;
import org.audiveris.proxymusic.PartList;
import org.audiveris.proxymusic.PartName;
import org.audiveris.proxymusic.Pitch;
import org.audiveris.proxymusic.ScorePart;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.ScorePartwise.Part;
import org.audiveris.proxymusic.ScorePartwise.Part.Measure;
import org.audiveris.proxymusic.Step;
import org.audiveris.proxymusic.Time;
import org.audiveris.proxymusic.Work;
import org.audiveris.proxymusic.util.Marshalling;
import org.audiveris.proxymusic.util.Marshalling.UnmarshallingException;
import org.juhanir.domain.MelodyNote;

/**
 * Contains logic for parsing MusicXML files and converting internal note representation back to
 * 'MelodyNote' objects that can be serialized to MusicXML notes.
 */
public class ScoreParser {

  private static Logger parserLogger = Logger.getLogger(ScoreParser.class.getName());
  private static String[] cicleOfFifthsMajor = new String[] { "Cb", "Gb", "Db", "Ab", "Eb", "Bb",
      "F", "C", "G", "D", "A", "E", "B", "F#", "C#" };
  private static String[] cicleOfFifthsMinor = new String[] { "Abm", "Ebm", "Bbm", "Fm", "Cm", "Gm",
      "Dm", "Am", "Em", "Bm", "F#m", "C#m", "G#m", "D#m", "A#m" };

  /**
   * <p>
   * Convert a MusicXML note to internal representation.
   * </p>
   *
   * @param step the note name
   * @param octave octave value
   * @param alter flat/sharp
   * @return note as integer representation
   */
  public int convertNoteToInt(String step, int octave, int alter) {
    if (step == null || !Constants.noteNames.contains(step)) {
      throw new IllegalArgumentException(String.format("Unsupported step value %s", step));
    }
    if (!Arrays.asList(-1, 0, 1).contains(alter)) {
      throw new IllegalArgumentException(String.format("Unsupported alter value %s", alter));
    }
    if (step.equals("C") && alter < 0) {
      throw new IllegalArgumentException("Flat C not implemented");
    }
    if (step.equals("B") && alter > 0) {
      throw new IllegalArgumentException("Sharp B not implemented");
    }
    if (octave < Constants.OCTAVE_LOWER_BOUND || octave > Constants.OCTAVE_UPPER_BOUND) {
      throw new IllegalArgumentException(String.format("Not supported octave value %s", octave));
    }
    return (octave - Constants.OCTAVE_LOWER_BOUND) * 12 + Constants.noteNames.indexOf(step) + alter;
  }

  /**
   * <p>
   * Convert note from integer format to something playable.
   * </p>
   * <p>
   * When it is not clear what the note is, e.g. the int value alone cannot say if half step above C
   * is C# or Db, check if key is flat and use (note)b, otherwise always use sharp.
   * </p>
   *
   * @param numericalNote numerical value of the note
   * @param musicalKey key of the tune
   * @return MelodyNote
   */
  public MelodyNote convertIntToNote(int numericalNote, String musicalKey) {

    int noteValue = numericalNote % 12;
    int octave = (int) Math.floor(numericalNote / 12) + Constants.OCTAVE_LOWER_BOUND;
    String stepValue = Constants.noteNames.get(noteValue);
    int alter = 0;
    if (stepValue == null) {
      if (this.isFlat(musicalKey)) {
        stepValue = Constants.noteNames.get(noteValue + 1);
        alter = -1;
      } else {
        stepValue = Constants.noteNames.get(noteValue - 1);
        alter = 1;
      }
    }

    return new MelodyNote(stepValue, alter, octave);
  }

  /**
   * Goes through a list of files and groups them per key.
   *
   * @param reader file reader
   * @param files list of file names
   * @return Map of filenames per key
   */
  public Map<String, List<String>> collectFilesPerKey(FileIo reader, List<String> files) {
    Map<String, List<String>> filesPerKey = new HashMap<>();
    for (String filePath : files) {
      try (InputStream is = reader.readFile(filePath)) {
        String tuneKey = this.getKeyForTune(is);
        if (!filesPerKey.containsKey(tuneKey)) {
          filesPerKey.put(tuneKey, new ArrayList<>());
        }
        filesPerKey.get(tuneKey).add(filePath);
      } catch (Exception e) {
        parserLogger.severe("Failed to collect from " + filePath);
        parserLogger.severe(e.toString());
      }
    }
    return filesPerKey;
  }

  /**
   * Resolves the musical key for a song.
   *
   * @param source song to parse as inputstream from a MusicXML file.
   * @return key name
   * @throws UnmarshallingException if cannot parse input to partwise score
   */
  public String getKeyForTune(InputStream source) throws UnmarshallingException {
    ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
    ArrayList<String> musicalKeys = new ArrayList<>();
    for (final Part part : scorePartwise.getPart()) {
      for (final Measure measure : part.getMeasure()) {
        List<Attributes> attributes =
            measure.getNoteOrBackupOrForward().stream().filter(Attributes.class::isInstance)
                .map(c -> (Attributes) c).collect(Collectors.toList());
        this.resolveKey(attributes, musicalKeys);
      }
    }
    if (musicalKeys.size() > 1) {
      parserLogger.info(
          String.format("Found %s musical keys: %s", musicalKeys.size(), musicalKeys.toString()));
      throw new IllegalArgumentException("Only one key is supported");
    }
    return musicalKeys.get(0);
  }

  /**
   * Parses a MusicXML source file and extracts the notes.
   *
   * @param source MusicXML file as inputstream.
   * @return List of integers representing the melody sequence.
   * @throws UnmarshallingException if cannot parse input to partwise score
   */
  public List<Integer> parse(InputStream source) throws UnmarshallingException {
    ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
    List<Part> parts = scorePartwise.getPart();
    ArrayList<Integer> melodySequence = new ArrayList<>();
    for (final Part part : parts) {
      List<Measure> measures = part.getMeasure();
      for (final Measure measure : measures) {
        // TODO: If two staves in measure, only get 1st?
        List<Object> measureContents = measure.getNoteOrBackupOrForward();
        List<Note> notes = measureContents.stream().filter(Note.class::isInstance)
            .map(c -> (Note) c).collect(Collectors.toList());
        for (final Note note : notes) {
          Pitch pitch = note.getPitch();
          String voice = note.getVoice();
          // Use voice = 1 check to avoid having to deal with backups for now
          if (pitch == null || !voice.equals("1")) {
            continue;
          }
          BigDecimal alter = Optional.ofNullable(pitch.getAlter()).orElse(BigDecimal.valueOf(0.0));
          Step step = pitch.getStep();
          int finalNote =
              this.convertNoteToInt(step.value(), pitch.getOctave(), alter.intValueExact());
          melodySequence.add(finalNote);
        }
      }
    }
    return melodySequence;
  }

  private void resolveKey(List<Attributes> attrs, List<String> musicalKeys) {
    for (Attributes attributes : attrs) {
      List<Key> keys = attributes.getKey();
      for (Key key : keys) {
        int fifths = key.getFifths().intValue();
        String mode = Optional.ofNullable(key.getMode()).orElse("major");
        if (fifths < (-1) * Constants.FIFTHS_SUPPORTED_RANGE
            || fifths > Constants.FIFTHS_SUPPORTED_RANGE) {
          throw new IllegalArgumentException(
              String.format("Non-supported fifths value %s", fifths));
        }
        if (!List.of("major", "minor").contains(mode)) {
          throw new IllegalArgumentException(String.format("Non-supported mode value %s", mode));
        }
        fifths += Constants.FIFTHS_SUPPORTED_RANGE; // normalize
        String musicKey =
            (mode.equals("major")) ? cicleOfFifthsMajor[fifths] : cicleOfFifthsMinor[fifths];
        if (!musicalKeys.contains(musicKey)) {
          musicalKeys.add(musicKey);
        }
      }
    }
  }

  /**
   * Converts melody as integer array to a ScorePartwise.
   *
   * @param melody the melody
   * @param musicalKey selected musical key
   * @return ScorePartwise
   */
  public ScorePartwise convertMelodyToScorePartwise(int[] melody, String musicalKey) {
    MelodyNote[] notes =
        Arrays.stream(melody).<MelodyNote>mapToObj(note -> this.convertIntToNote(note, musicalKey))
            .toArray(MelodyNote[]::new);
    try {
      BigInteger fifths = new BigInteger(String.valueOf(this.getFifths(musicalKey)));
      if (fifths.intValue() < -7) {
        throw new Exception(String.format("Could not resolve fifths value from %s", musicalKey));
      }
      return this.getScorePartwise(notes, fifths);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  private boolean isFlat(String musicalKey) {
    for (int i = 0; i < cicleOfFifthsMajor.length; i++) {
      if (cicleOfFifthsMajor[i].equals(musicalKey) && i < Constants.FIFTHS_SUPPORTED_RANGE
          || cicleOfFifthsMinor[i].equals(musicalKey) && i < Constants.FIFTHS_SUPPORTED_RANGE) {
        return true;
      }
    }
    return false;
  }

  private int getFifths(String musicalKey) {
    int res = Integer.MIN_VALUE;
    for (int i = 0; i < cicleOfFifthsMajor.length; i++) {
      if (cicleOfFifthsMajor[i].equals(musicalKey) || cicleOfFifthsMinor[i].equals(musicalKey)) {
        res = (-1) * Constants.FIFTHS_SUPPORTED_RANGE + i;
        break;
      }
    }
    return res;
  }

  private ScorePartwise getScorePartwise(MelodyNote[] notes, BigInteger fifths) {
    // Generated factory for all proxymusic elements
    ObjectFactory factory = new ObjectFactory();

    // Allocate the score partwise
    ScorePartwise scorePartwise = factory.createScorePartwise();

    // Work
    Work work = factory.createWork();
    scorePartwise.setWork(work);
    work.setWorkTitle("Title for the work");
    work.setWorkNumber("Number for the work");

    // PartList
    PartList partList = factory.createPartList();
    scorePartwise.setPartList(partList);

    // Scorepart in partList
    ScorePart scorePart = factory.createScorePart();
    partList.getPartGroupOrScorePart().add(scorePart);
    scorePart.setId("P1");

    PartName partName = factory.createPartName();
    scorePart.setPartName(partName);
    partName.setValue("Music");

    // ScorePart in scorePartwise
    ScorePartwise.Part part = factory.createScorePartwisePart();
    scorePartwise.getPart().add(part);
    part.setId(scorePart);

    // Measure
    Measure measure = factory.createScorePartwisePartMeasure();
    part.getMeasure().add(measure);
    measure.setNumber("1");

    // Attributes
    Attributes attributes = factory.createAttributes();
    measure.getNoteOrBackupOrForward().add(attributes);

    // Divisions
    attributes.setDivisions(new BigDecimal(1));

    // Key
    Key key = factory.createKey();
    attributes.getKey().add(key);
    key.setFifths(fifths);

    // Time
    Time time = factory.createTime();
    attributes.getTime().add(time);
    time.getTimeSignature().add(factory.createTimeBeats("4"));
    time.getTimeSignature().add(factory.createTimeBeatType("4"));

    for (MelodyNote melodyNote : notes) {

      Note note = factory.createNote();
      measure.getNoteOrBackupOrForward().add(note);

      Pitch pitch = factory.createPitch();
      note.setPitch(pitch);
      pitch.setStep(melodyNote.getStepEnum());
      pitch.setOctave(melodyNote.getOctave());
      pitch.setAlter(new BigDecimal(melodyNote.getAlter()));

      note.setDuration(new BigDecimal(4));

      NoteType type = factory.createNoteType();
      type.setValue("whole");
      note.setType(type);
    }

    return scorePartwise;
  }

}
