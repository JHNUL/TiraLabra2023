package org.juhanir.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

/**
 * Utility class for filesystem operations.
 */
public class FileIo {

  private static final Logger fileLogger = LogManager.getLogger();

  public InputStream readFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(new File(filePath));
  }

  public FileInputStream readFile(String folderPath, String fileName) throws FileNotFoundException {
    String path = this.getFolderPath(folderPath);
    return new FileInputStream(new File(path + File.separator + fileName));
  }

  public String readFileAsString(String folderPath, String fileName) throws IOException {
    String path = this.getFolderPath(folderPath);
    return Files.readString(Path.of(path + File.separator + fileName));
  }

  /**
   * Collect all files in the specified folder.
   *
   * @param folderPath path to the folder
   * @return list of filepaths as string
   */
  public List<String> getAllFilePathsInFolder(String folderPath) {
    try {
      String path = this.getFolderPath(folderPath);
      File dataFolder = new File(path);
      return Arrays.stream(dataFolder.listFiles()).map(File::getAbsolutePath)
          .collect(Collectors.toList());
    } catch (Exception e) {
      fileLogger.error(e);
      return new ArrayList<>();
    }
  }

  /**
   * Collect all files with certain extension in the specified folder.
   *
   * @param folderPath path to the folder
   * @param extension  file extension
   * @return list of filepaths as string
   */
  public List<String> getAllFilePathsInFolder(String folderPath, String extension) {
    try {
      String path = this.getFolderPath(folderPath);
      File dataFolder = new File(path);
      return Arrays.stream(dataFolder.listFiles())
          .filter(file -> file.getName().endsWith(extension))
          .map(File::getAbsolutePath)
          .collect(Collectors.toList());
    } catch (Exception e) {
      fileLogger.error(e);
      return new ArrayList<>();
    }
  }

  /**
   * Write string content to a file.
   *
   * @param folderPath path to the folder
   * @param fileName   file name
   * @param content    content
   * @throws IOException if write fails
   */
  public void writeToFile(String folderPath, String fileName, String content) throws IOException {
    String path = this.getFolderPath(folderPath);
    try (PrintWriter pw = new PrintWriter(
        new FileWriter(String.format("%s%s%s", path, File.separator, fileName)))) {
      pw.print(content);
    }
  }

  /**
   * Write a MIDI file based on the provided jfugue Pattern
   *
   * @param folderPath    path to the folder
   * @param fileName      file name
   * @param melodyPattern jfugue Staccato pattern
   * @throws IOException if write fails
   */
  public void saveMidiFile(String folderPath, String fileName, Pattern melodyPattern) throws IOException {
    String path = this.getFolderPath(folderPath);
    File file = new File(String.format("%s%s%s", path, File.separator, fileName));
    MidiFileManager.savePatternToMidi(melodyPattern, file);
  }

  private String getFolderPath(String folderPath) throws FileNotFoundException {

    // This will work with the intended source data folder
    // structure when using the built jar
    Path dataPath = Paths.get(folderPath).toAbsolutePath();
    File dataFolder = dataPath.toFile();
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      fileLogger.info(String.format("Using path %s", dataPath.toString()));
      return dataPath.toString();
    }

    // This is for mvn javafx launcher
    Path mvnPath = Paths.get("").toAbsolutePath().getParent().resolve(folderPath);
    File mvnDataFolder = mvnPath.toFile();
    if (mvnDataFolder.exists() && mvnDataFolder.isDirectory()) {
      fileLogger.info(String.format("Using path %s", mvnPath.toString()));
      return mvnPath.toString();
    }

    throw new FileNotFoundException();
  }

}
