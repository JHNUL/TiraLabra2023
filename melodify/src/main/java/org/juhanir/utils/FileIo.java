package org.juhanir.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for filesystem operations.
 */
public class FileIo {

  public InputStream readFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(new File(filePath));
  }

  /**
   * Collect all files in the specified folder.
   *
   * @param folderPath path to the folder
   * @return list of filepaths as string
   */
  public List<String> getAllFilePathsInFolder(String folderPath) {
    // TODO: Change to work with built project as well
    String path = this.getFolderPath(folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      return Arrays.stream(dataFolder.listFiles()).map(File::getAbsolutePath)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * Write string content to a file.
   *
   * @param folderPath path to the folder
   * @param fileName file name
   * @param content content
   * @throws IOException if write fails
   */
  public void writeToFile(String folderPath, String fileName, String content) throws IOException {
    String path = this.getFolderPath(folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      PrintWriter pw =
          new PrintWriter(new FileWriter(String.format("%s%s%s", path, File.separator, fileName)));
      pw.print(content);
      pw.close();
    }
  }

  private String getFolderPath(String folderPath) {
    String parentPath = Paths.get(System.getProperty("user.dir")).getParent().toString();
    return String.format("%s%s%s", parentPath, File.separator, folderPath);
  }

}
