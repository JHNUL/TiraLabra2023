package org.juhanir.services;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;

/**
 * Contains methods to train the model.
 */
public class TrainingService {

  private static Logger trainingLogger =
      Logger.getLogger(TrainingService.class.getName());

  private final FileIo fileIo;
  private final ScoreParser scoreParser;
  private Trie trie;

  /**
   * Constructor.
   *
   * @param fileIo file utility
   * @param scoreParser score parser
   * @param trie the data structure for the model
   */
  public TrainingService(FileIo fileIo, ScoreParser scoreParser, Trie trie) {
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

    for (final String filePath : filePaths) {
      try (InputStream is = this.fileIo.readFile(filePath)) {
        List<Integer> melodies = this.scoreParser.parse(is);
        trainingLogger.info(melodies.toString());
        trainingLogger.info(String.format("Len melodies: %s", melodies.size()));
        for (int i = 0; i < melodies.size() - degree; i++) {
          int[] trainingTuple = melodies.subList(i, i + degree + 1).stream()
              .mapToInt(Integer::intValue).toArray();
          trainingLogger.info(String.format("Inserting: %s", Arrays.toString(trainingTuple)));
          this.trie.insert(trainingTuple);
        }
      } catch (Exception e) {
        trainingLogger.severe("Failed to parse file " + filePath);
        trainingLogger.severe(e.toString());
      }

    }

  }
}
