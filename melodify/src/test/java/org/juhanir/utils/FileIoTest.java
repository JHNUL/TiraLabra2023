package org.juhanir.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class FileIoTest {

  // File sourceFile = new File("src/test/resources/" + file);

  @Test
  void readFileReturnsFileInputStream() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.readFile("src/test/resources/g99.xml");
      assertInstanceOf(FileInputStream.class, res);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void readFileThrowsWhenNotFound() {
    FileIo fileIo = new FileIo();
    assertThrows(FileNotFoundException.class,
        () -> fileIo.readFile("src/test/resources/wilhelmscream.xml"));
  }

  @Test
  void readFileOverload1ReturnsFileInputStream() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.readFile("src/test/resources", "g99.xml");
      assertInstanceOf(FileInputStream.class, res);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void readFileOverload1ThrowsWhenNotFound() {
    FileIo fileIo = new FileIo();
    assertThrows(FileNotFoundException.class,
        () -> fileIo.readFile("src/test/resources", "wilhelmscream.xml"));
  }

  @Test
  void readFileAsString() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.readFileAsString("src/test/resources/", "hello.txt");
      assertEquals("well hello!", res);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void readFileAsStringThrowsWhenNotFound() {
    FileIo fileIo = new FileIo();
    assertThrows(IOException.class,
        () -> fileIo.readFileAsString("src/test/resources", "wilhelmscream.xml"));
  }

  @Test
  void getAllFilePathsInFolder() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.getAllFilePathsInFolder("src/test/resources/")
          .stream()
          .map(f -> Paths.get(f).getFileName().toString())
          .collect(Collectors.toList());
      assertInstanceOf(List.class, res);
      List<String> expected = List.of(
          "a07.xml",
          "hello.txt",
          "alphabet-song.xml",
          "g118.xml",
          "a83.xml",
          "fifths-large.xml",
          "multi-keys.xml",
          "a34.xml",
          "g112.xml",
          "fifths-small.xml",
          "g99.xml",
          "unsupported-mode.xml");
      for (String exp : expected) {
        assertTrue(res.contains(exp));
      }
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void getAllFilePathsInFolderDoesNotThrow() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.getAllFilePathsInFolder("not/a/real/path/foo/bar")
          .stream()
          .map(f -> Paths.get(f).getFileName().toString())
          .collect(Collectors.toList());
      assertInstanceOf(List.class, res);
      assertEquals(0, res.size());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void getAllFilePathsInFolderOverload1() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.getAllFilePathsInFolder("src/test/resources/", ".txt")
          .stream()
          .map(f -> Paths.get(f).getFileName().toString())
          .collect(Collectors.toList());
      assertInstanceOf(List.class, res);
      assertIterableEquals(List.of("hello.txt"), res);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void getAllFilePathsInFolderDoesNotThrowOverload1() {
    try {
      FileIo fileIo = new FileIo();
      var res = fileIo.getAllFilePathsInFolder("not/a/real/path/foo/bar", ".txt")
          .stream()
          .map(f -> Paths.get(f).getFileName().toString())
          .collect(Collectors.toList());
      assertInstanceOf(List.class, res);
      assertEquals(0, res.size());
    } catch (Exception e) {
      fail(e);
    }
  }

}
