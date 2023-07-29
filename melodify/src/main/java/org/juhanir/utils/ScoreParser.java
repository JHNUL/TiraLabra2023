package org.juhanir.utils;

import java.io.InputStream;
import java.math.BigDecimal;
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
import org.audiveris.proxymusic.Pitch;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.ScorePartwise.Part;
import org.audiveris.proxymusic.ScorePartwise.Part.Measure;
import org.audiveris.proxymusic.Step;
import org.audiveris.proxymusic.util.Marshalling;
import org.audiveris.proxymusic.util.Marshalling.UnmarshallingException;

public class ScoreParser {

    private static Logger parserLogger = Logger.getLogger(ScoreParser.class.getName());
    private static List<String> noteNamesToInt = Arrays.asList("C", null, "D", null, "E", "F", null, "G", null, "A",
            null, "B");
    private static String[] cicleOfFifths = new String[] { "F#", "C#", "Ab", "Eb", "Bb", "F", "C", "G", "D", "A",
            "E", "B", "F#" };

    public int convertNoteToInt(String step, int octave, int alter) {
        if (step == null || !noteNamesToInt.contains(step)) {
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
        return (octave - Constants.OCTAVE_LOWER_BOUND) * 12 + noteNamesToInt.indexOf(step) + alter;
    }

    public Map<String, List<String>> collectFilesPerKey(FileIO reader, List<String> files) {
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

    public String getKeyForTune(InputStream source) throws UnmarshallingException {
        ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
        ArrayList<String> musicalKeys = new ArrayList<>();
        for (final Part part : scorePartwise.getPart()) {
            for (final Measure measure : part.getMeasure()) {
                List<Attributes> attributes = measure.getNoteOrBackupOrForward().stream()
                        .filter(Attributes.class::isInstance)
                        .map(c -> (Attributes) c).collect(Collectors.toList());
                this.resolveKey(attributes, musicalKeys);
            }
        }
        if (musicalKeys.size() > 1) {
            parserLogger.info(String.format("Found %s musical keys: %s", musicalKeys.size(), musicalKeys.toString()));
            throw new IllegalArgumentException("Only one key is supported");
        }
        return musicalKeys.get(0);
    }

    public List<Integer> parse(InputStream source) throws UnmarshallingException {
        ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
        List<Part> parts = scorePartwise.getPart();
        ArrayList<Integer> melodySequence = new ArrayList<>();
        for (final Part part : parts) {
            List<Measure> measures = part.getMeasure();
            for (final Measure measure : measures) {
                // TODO: If two staves in measure, only get 1st?
                List<Object> measureContents = measure.getNoteOrBackupOrForward();
                List<Note> notes = measureContents.stream().filter(Note.class::isInstance).map(c -> (Note) c)
                        .collect(Collectors.toList());
                for (final Note note : notes) {
                    Pitch pitch = note.getPitch();
                    String voice = note.getVoice();
                    // Use voice = 1 check to avoid having to deal with backups for now
                    if (pitch == null || !voice.equals("1")) {
                        continue;
                    }
                    BigDecimal alter = Optional.ofNullable(pitch.getAlter()).orElse(BigDecimal.valueOf(0.0));
                    Step step = pitch.getStep();
                    int finalNote = this.convertNoteToInt(step.value(), pitch.getOctave(), alter.intValueExact());
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
                if (fifths < (-1) * Constants.FIFTHS_SUPPORTED_RANGE || fifths > Constants.FIFTHS_SUPPORTED_RANGE) {
                    throw new IllegalArgumentException(String.format("Non-supported fifths value %s", fifths));
                }
                fifths += Constants.FIFTHS_SUPPORTED_RANGE; // normalize
                String mKey = cicleOfFifths[fifths];
                parserLogger.info(String.format("Resolved musical key %s", mKey));
                if (!musicalKeys.contains(mKey)) {
                    musicalKeys.add(mKey);
                }
            }
        }
    }

}
