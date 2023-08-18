package org.juhanir.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.util.Marshalling;
import org.audiveris.proxymusic.util.Marshalling.MarshallingException;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;

/**
 * Utility class for filesystem operations.
 */
public class FileIo {

  public InputStream readFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(new File(filePath));
  }

  public FileInputStream readFile(String folderPath, String fileName) throws FileNotFoundException {
    String path = this.getFolderPath(folderPath);
    return new FileInputStream(new File(path + File.separator + fileName));
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
   * Collect all files with certain extension in the specified folder.
   *
   * @param folderPath path to the folder
   * @param extension  file extension
   * @return list of filepaths as string
   */
  public List<String> getAllFilePathsInFolder(String folderPath, String extension) {
    // TODO: Change to work with built project as well
    String path = this.getFolderPath(folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      return Arrays.stream(dataFolder.listFiles())
          .filter(file -> file.getName().endsWith(extension))
          .map(File::getAbsolutePath)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
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
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      try (PrintWriter pw = new PrintWriter(
          new FileWriter(String.format("%s%s%s", path, File.separator, fileName)))) {
        pw.print(content);
      }
    } else {
      throw new IOException("The specified folder does not exist.");
    }
  }

  /**
   * Write ScorePartwise content to a file.
   *
   * @param folderPath path to the folder
   * @param fileName   file name
   * @param score      content in ScorePartwise format
   * @throws IOException          if write fails
   * @throws MarshallingException if marshalling the scorepartwise to xml fails
   */
  public void writeToFile(String folderPath, String fileName, ScorePartwise score)
      throws IOException, MarshallingException {
    String path = this.getFolderPath(folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      try (OutputStream os = new FileOutputStream(new File(dataFolder, fileName))) {
        Marshalling.marshal(score, os, true, 2);
      }
    } else {
      throw new IOException("The specified folder does not exist.");
    }
  }

  /**
   * Write a MIDI file based on the provided jfugue Pattern
   *
   * @param folderPath    path to the folder
   * @param fileName      file name
   * @param melodyPattern jfugue patterh
   * @throws IOException  if write fails
   */
  public void saveMidiFile(String folderPath, String fileName, Pattern melodyPattern)
      throws IOException {
    String path = this.getFolderPath(folderPath);
    File dataFolder = new File(path);
    if (dataFolder.exists() && dataFolder.isDirectory()) {
      try {
        File file = new File(String.format("%s%s%s", path, File.separator, fileName));
        MidiFileManager.savePatternToMidi(melodyPattern, file);
      } catch (Exception e) {
        throw e;
      }
    } else {
      throw new IOException("The specified folder does not exist.");
    }
  }

  private String getFolderPath(String folderPath) {
    String parentPath = Paths.get(System.getProperty("user.dir")).getParent().toString();
    return String.format("%s%s%s", parentPath, File.separator, folderPath);
  }

}
