package org.juhanir.services;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;

/**
 * Contains methods to train the model
 */
public class TrainingService {

    private static Logger trainingLogger = Logger.getLogger(TrainingService.class.getName());

    private final FileIO fileIo;
    private final ScoreParser scoreParser;

    public TrainingService(FileIO fileIo, ScoreParser scoreParser) {
        this.fileIo = fileIo;
        this.scoreParser = scoreParser;
    }

    /**
     * Train the model with the specified data
     * 
     * @param filePaths list of paths to musicxml files
     */
    public void trainWith(List<String> filePaths) {

        trainingLogger.info(String.format("Training with files %s", filePaths.toString()));

        for (final String filePath : filePaths) {
            try (InputStream is = this.fileIo.readFile(filePath)) {
                List<String> melodies = this.scoreParser.parse(is);
                String msg = melodies.toString();
                trainingLogger.info(msg);
                // TODO: populate Trie with the melodie n-tuples based on degree of Markov Chain
                // we're using
            } catch (Exception e) {
                trainingLogger.severe("Failed to parse file " + filePath);
                trainingLogger.severe(e.toString());
            }

        }

    }
}
