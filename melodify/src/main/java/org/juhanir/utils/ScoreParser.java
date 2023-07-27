package org.juhanir.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

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
        if (octave < 3 || octave > 5) {
            throw new IllegalArgumentException("Only octaves 3-5 implemented");
        }
        return (octave - 3) * 12 + noteNamesToInt.indexOf(step) + alter;
    }

    public List<Integer> parse(InputStream source) throws UnmarshallingException {
        ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
        List<Part> parts = scorePartwise.getPart();
        ArrayList<Integer> melodySequence = new ArrayList<>();
        for (final Part part : parts) {
            List<Measure> measures = part.getMeasure();
            for (final Measure measure : measures) {
                // TODO: If two staves in measure, only get 1st?
                List<Object> things = measure.getNoteOrBackupOrForward();
                for (final Object thing : things) {
                    if (thing instanceof Note) {
                        Note noteThing = (Note) thing;
                        String voice = noteThing.getVoice();
                        // Use voice = 1 check to avoid having to deal with backups for now
                        if (!voice.equals("1"))
                            continue;
                        Pitch pitch = noteThing.getPitch();
                        BigDecimal alter = Optional.ofNullable(pitch.getAlter()).orElse(BigDecimal.valueOf(0.0));
                        Step step = pitch.getStep();
                        int note = this.convertNoteToInt(step.value(), pitch.getOctave(), alter.intValueExact());
                        melodySequence.add(note);
                        parserLogger.info(String.format("Added note %s", note));
                    }
                }
            }
        }
        return melodySequence;
    }

}
