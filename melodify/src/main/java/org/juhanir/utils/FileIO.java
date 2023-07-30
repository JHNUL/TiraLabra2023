package org.juhanir.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for filesystem operations.
 */
public class FileIO {

  public InputStream readFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(new File(filePath));
  }

  public List<String> getAllFilePathsInFolder(String folderPath) {
    // TODO: Change to work with built project as well
    String workingDir =
        Paths.get(System.getProperty("user.dir")).getParent().toString();
    String path =
        String.format("%s%s%s", workingDir, File.separator, folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      return Arrays.stream(dataFolder.listFiles()).map(File::getAbsolutePath)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

}
