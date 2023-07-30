package org.juhanir.services;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.juhanir.domain.Trie;
import org.juhanir.utils.Constants;
import org.juhanir.utils.FileIO;
import org.juhanir.utils.ScoreParser;

public class TrainingService {

  private static Logger trainingLogger =
      Logger.getLogger(TrainingService.class.getName());

  private final FileIO fileIo;
  private final ScoreParser scoreParser;
  private Trie trie;

  public TrainingService(FileIO fileIo, ScoreParser scoreParser, Trie trie) {
    this.fileIo = fileIo;
    this.scoreParser = scoreParser;
    this.trie = trie;
  }

  /**
   * Train the model with the specified data.
   *
   * @param filePaths list of paths to musicxml files
   * @param degree degree of Markov Chain to use
   */
  public void trainWith(List<String> filePaths, int degree) {

    trainingLogger
        .info(String.format("Training with files %s", filePaths.toString()));
    trainingLogger
        .info(String.format("Note array size %s", Constants.NOTE_ARRAY_SIZE));

    for (final String filePath : filePaths) {
      try (InputStream is = this.fileIo.readFile(filePath)) {
        List<Integer> melodies = this.scoreParser.parse(is);
        trainingLogger.info(melodies.toString());
        trainingLogger.info(String.format("Len melodies: %s", melodies.size()));
        for (int i = 0; i < melodies.size() - degree; i++) {
          int[] trainingTuple = melodies.subList(i, i + degree + 1).stream()
              .mapToInt(Integer::intValue).toArray();
          this.trie.insert(trainingTuple);
          trainingLogger.info(
              String.format("Inserted %s", Arrays.toString(trainingTuple)));
        }
      } catch (Exception e) {
        trainingLogger.severe("Failed to parse file " + filePath);
        trainingLogger.severe(e.toString());
      }

    }

  }

  /**
   * Clear the existing trie.
   */
  public void clear() {
    this.trie = new Trie();
  }
}
