package org.juhanir.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.audiveris.proxymusic.util.Marshalling.UnmarshallingException;
import org.juhanir.domain.Trie;
import org.juhanir.utils.FileIo;
import org.juhanir.utils.ScoreParser;

/**
 * Contains methods to train the model.
 */
public class TrainingService {

  private static Logger trainingLogger = LogManager.getLogger();

  private final FileIo fileIo;
  private final ScoreParser scoreParser;
  private Trie trie;

  /**
   * Constructor.
   *
   * @param fileIo      file utility
   * @param scoreParser score parser
   * @param trie        the data structure for the model
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
   * @param degree    degree of Markov Chain to use
   */
  public void trainWith(List<String> filePaths, int degree) {
    trainingLogger.info(
        String.format(
            "Training model with degree %s and %s source data files", degree, filePaths.size()));
    for (final String filePath : filePaths) {
      try (InputStream is = this.fileIo.readFile(filePath)) {
        List<Integer> melodies = this.scoreParser.parse(is);
        for (int i = 0; i < melodies.size() - degree; i++) {
          int[] trainingTuple = melodies
              .subList(i, i + degree + 1)
              .stream()
              .mapToInt(Integer::intValue)
              .toArray();
          this.trie.insert(trainingTuple);
        }
      } catch (UnmarshallingException | IOException | IllegalArgumentException e) {
        trainingLogger.error("Failed to parse file " + filePath);
        trainingLogger.error(e);
      }
    }
  }
}
