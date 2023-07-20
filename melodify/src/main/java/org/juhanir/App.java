package org.juhanir;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.audiveris.proxymusic.Note;
import org.audiveris.proxymusic.NoteType;
import org.audiveris.proxymusic.Pitch;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.ScorePartwise.Part;
import org.audiveris.proxymusic.ScorePartwise.Part.Measure;
import org.audiveris.proxymusic.Step;
import org.audiveris.proxymusic.util.Marshalling;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    // TODO: Just a test method, do better implementation with proper project structure
    private void readFileAndParse(String fPath) {
        try (InputStream is = new FileInputStream(new File(fPath))) {
            ScorePartwise scorePartwise = (ScorePartwise) Marshalling.unmarshal(is);
            List<Part> parts = scorePartwise.getPart();
            for (final Part part : parts) {
                List<Measure> measures = part.getMeasure();
                for (final Measure measure : measures) {
                    // TODO: If two staves in measure, only get 1st?
                    List<Object> things = measure.getNoteOrBackupOrForward();
                    for (final Object thing : things) {
                        if (thing instanceof Note) {
                            Note noteThing = (Note) thing;
                            Pitch pitch = noteThing.getPitch();
                            int octave = pitch.getOctave();
                            Step step = pitch.getStep();
                            String stepValue = step.value();
                            // TODO: Use voice = 1 check to avoid having to deal with backups for now
                            String voice = noteThing.getVoice();
                            NoteType type = noteThing.getType();
                            String duration = type.getValue();
                            // TODO: use a logger to replace all sysouts
                            System.out.println(String.format("Step %s, octave %s, voice %s, duration %s", stepValue,
                                    octave, voice, duration));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Melodify");
        TextField textField = new TextField();
        Button button = new Button("Parse file");
        button.setOnAction(event -> this.readFileAndParse(textField.getText().strip()));
        Scene scene = new Scene(new VBox(label, button, textField), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
