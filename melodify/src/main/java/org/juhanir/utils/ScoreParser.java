package org.juhanir.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.audiveris.proxymusic.Note;
import org.audiveris.proxymusic.NoteType;
import org.audiveris.proxymusic.Pitch;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.ScorePartwise.Part;
import org.audiveris.proxymusic.ScorePartwise.Part.Measure;
import org.audiveris.proxymusic.Step;
import org.audiveris.proxymusic.util.Marshalling;
import org.audiveris.proxymusic.util.Marshalling.UnmarshallingException;

public class ScoreParser {

    private static Logger parserLogger = Logger.getLogger(ScoreParser.class.getName());

    public List<String> parse(InputStream source) throws UnmarshallingException {
        ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(source);
        List<Part> parts = scorePartwise.getPart();
        ArrayList<String> melodySequence = new ArrayList<>();
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
                        int octave = pitch.getOctave();
                        Step step = pitch.getStep();
                        String stepValue = step.value();
                        NoteType type = noteThing.getType();
                        String duration = type.getValue();
                        String note = String.format("%s%s%s", stepValue, octave, duration);
                        melodySequence.add(note);
                        parserLogger.info(String.format("Added note %s", note));
                    }
                }
            }
        }
        return melodySequence;
    }

}
