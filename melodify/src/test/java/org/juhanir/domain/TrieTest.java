package org.juhanir.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.juhanir.Constants;
import org.junit.jupiter.api.Test;

class TrieTest {

  private final int[] oneChild = { 1 };
  private final int[] twoChildren = { 1, 2 };
  private final int[] threeChildren = { 1, 2, 3 };
  private final int[] firstBranch = { 5, 6, 7 };
  private final int[] secondBranch = { 5, 6, 8 };
  private final int[] thirdBranch = { 5, 6, 9 };
  private final int[] fourthBranch = { 5, 6, 10 };
  private final int[] fifthBranch = { 5, 7, 11 };

  @Test
  void canInstantiate() {
    assertNotNull(new Trie());
  }

  @Test
  void canInsertNode() {
    Trie trie = new Trie();
    trie.insert(oneChild);
    assertEquals(2, trie.size());
  }

  @Test
  void canInsertNodes() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    assertEquals(4, trie.size());
  }

  @Test
  void doesNotInsertDuplicates() {
    Trie trie = new Trie();
    trie.insert(oneChild);
    assertEquals(2, trie.size());
    trie.insert(oneChild);
    assertEquals(2, trie.size());
    trie.insert(twoChildren);
    assertEquals(3, trie.size());
    trie.insert(threeChildren);
    assertEquals(4, trie.size());
  }

  @Test
  void lookupWithValidKey() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    TrieNode last = trie.lookup(threeChildren);
    assertInstanceOf(TrieNode.class, last);
    assertEquals(threeChildren[2], last.getValue());
    trie = new Trie();
    trie.insert(twoChildren);
    last = trie.lookup(twoChildren);
    assertInstanceOf(TrieNode.class, last);
    assertEquals(threeChildren[1], last.getValue());
    trie = new Trie();
    trie.insert(oneChild);
    last = trie.lookup(oneChild);
    assertInstanceOf(TrieNode.class, last);
    assertEquals(threeChildren[0], last.getValue());
  }

  @Test
  void lookupWithInValidKey() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    assertNull(trie.lookup(new int[] { 35 }));
  }

  @Test
  void prefixSearchReturnsEmptyListWhenNotFound() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    TrieNode[] result = trie.prefixSearch(new int[] { 7, 6, 5, 4 });
    assertEquals(0, result.length);
  }

  @Test
  void emptyPrefixSearchReturnsChildrenOfRoot() {
    Trie trie = new Trie();
    trie.insert(new int[] { 1, 2 });
    trie.insert(new int[] { 2, 2 });
    trie.insert(new int[] { 3, 2 });
    TrieNode[] result = trie.prefixSearch(new int[] {});
    assertEquals(Constants.NOTE_ARRAY_SIZE, result.length);
    for (int i = 0; i < result.length; i++) {
      if (i == 1) {
        assertEquals(1, result[i].getValue());
        assertEquals(1, result[i].getCount());
      } else if (i == 2) {
        assertEquals(2, result[i].getValue());
        assertEquals(1, result[i].getCount());
      } else if (i == 3) {
        assertEquals(3, result[i].getValue());
        assertEquals(1, result[i].getCount());
      } else {
        assertNull(result[i]);
      }
    }
  }

  @Test
  void prefixSearchReturnsImmediateChildrenWithOneNotePrefix() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    TrieNode[] result = trie.prefixSearch(new int[] { threeChildren[0] });
    assertEquals(threeChildren[1], result[threeChildren[1]].getValue());
  }

  @Test
  void prefixSearchReturnsImmediateChildrenWithTwoNotePrefix() {
    Trie trie = new Trie();
    trie.insert(threeChildren);
    TrieNode[] result = trie.prefixSearch(new int[] { threeChildren[0], threeChildren[1] });
    assertEquals(threeChildren[2], result[threeChildren[2]].getValue());
  }

  @Test
  void prefixSearchReturnsImmediateChildrenWhenExists() {
    Trie trie = new Trie();
    trie.insert(firstBranch);
    trie.insert(secondBranch);
    trie.insert(thirdBranch);
    trie.insert(fourthBranch);
    trie.insert(fifthBranch);
    TrieNode[] result = trie.prefixSearch(new int[] { 5, 6 });
    long kids = Arrays.stream(result).filter(Objects::nonNull).count();
    assertEquals(4, kids);
    assertEquals(7, result[7].getValue());
    assertEquals(8, result[8].getValue());
    assertEquals(9, result[9].getValue());
    assertEquals(10, result[10].getValue());
  }

  @Test
  void prefixSearchReturnsImmediateChildrenNoDuplicates() {
    Trie trie = new Trie();
    trie.insert(firstBranch);
    trie.insert(secondBranch);
    trie.insert(thirdBranch);
    trie.insert(fourthBranch);
    trie.insert(fifthBranch);
    TrieNode[] result = trie.prefixSearch(new int[] { 5 });
    long kids = Arrays.stream(result).filter(Objects::nonNull).count();
    assertEquals(2, kids);
    assertEquals(6, result[6].getValue());
    assertEquals(7, result[7].getValue());
  }

  @Test
  void childrenHaveCorrectCounts() {
    Trie trie = new Trie();
    trie.insert(firstBranch);
    trie.insert(secondBranch);
    trie.insert(thirdBranch);
    trie.insert(fourthBranch);
    trie.insert(fifthBranch);
    TrieNode[] result = trie.prefixSearch(new int[] { 5 });
    assertEquals(4, result[6].getCount());
    assertEquals(1, result[7].getCount());
    result = trie.prefixSearch(new int[] { 5, 7 });
    long kids = Arrays.stream(result).filter(Objects::nonNull).count();
    assertEquals(1, kids);
    assertEquals(1, result[11].getCount());
  }

  @Test
  void treeHasCorrectSize() {
    Trie trie = new Trie();
    assertEquals(1, trie.size());
    trie.insert(firstBranch);
    assertEquals(4, trie.size());
    trie.insert(secondBranch);
    assertEquals(5, trie.size());
    trie.insert(thirdBranch);
    assertEquals(6, trie.size());
    trie.insert(fourthBranch);
    assertEquals(7, trie.size());
    trie.insert(fifthBranch);
    assertEquals(9, trie.size());
  }

  @Test
  void resolvesProbabilitiesCorrectlyWithOneChild() {
    Trie trie = new Trie();
    trie.insert(oneChild);
    TrieNode[] children = trie.prefixSearch(new int[] {});
    double[] probs = trie.getProbabilities(children);
    assertEquals(Constants.NOTE_ARRAY_SIZE, probs.length);
    assertEquals(1.0, probs[1]);
  }

  @Test
  void resolvesProbabilitiesCorrectlyWithManyChildren() {
    Trie trie = new Trie();
    trie.insert(firstBranch);
    trie.insert(secondBranch);
    trie.insert(thirdBranch);
    trie.insert(fourthBranch);
    trie.insert(fifthBranch);
    TrieNode[] children = trie.prefixSearch(new int[] { 5 });
    double[] probs = trie.getProbabilities(children);
    assertEquals(0.8, probs[6]);
    assertEquals(0.2, probs[7]);
  }

  @Test
  void trieCanBeCleared() {
    Trie trie = new Trie();
    trie.insert(firstBranch);
    trie.insert(secondBranch);
    trie.insert(thirdBranch);
    trie.insert(fourthBranch);
    trie.insert(fifthBranch);
    assertEquals(9, trie.size());
    trie.clear();
    assertEquals(1, trie.size());
  }

  @Test
  void producesMostCommonSequence() {
    Trie trie = new Trie();
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 9 });
    trie.insert(new int[] { 5, 8, 7, 8, 8, 8, 9 });
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 10 });
    trie.insert(new int[] { 5, 9, 8, 8, 8, 8, 9 });
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 9 });
    trie.insert(new int[] { 5, 5, 7, 7, 8, 9, 10 });
    trie.insert(new int[] { 5, 5, 7, 7, 8, 8, 9 });
    trie.insert(new int[] { 5, 5, 7, 7, 10, 8, 9 });
    trie.insert(new int[] { 5, 5, 7, 7, 8, 8, 9 });
    int[] seq = trie.getMostCommonSequenceStartingWith(5, 2);
    assertArrayEquals(new int[] {5, 5}, seq);
    seq = trie.getMostCommonSequenceStartingWith(5, 3);
    assertArrayEquals(new int[] {5, 5, 7}, seq);
    seq = trie.getMostCommonSequenceStartingWith(5, 4);
    assertArrayEquals(new int[] {5, 5, 7, 7}, seq);
    seq = trie.getMostCommonSequenceStartingWith(5, 5);
    assertArrayEquals(new int[] {5, 5, 7, 7, 8}, seq);
    seq = trie.getMostCommonSequenceStartingWith(5, 6);
    assertArrayEquals(new int[] {5, 5, 7, 7, 8, 8}, seq);
    seq = trie.getMostCommonSequenceStartingWith(5, 7);
    assertArrayEquals(new int[] {5, 5, 7, 7, 8, 8, 9}, seq);
  }

  @Test
  void producesMostCommonSequenceWithExceedingLength() {
    Trie trie = new Trie();
    trie.insert(new int[] { 5, 6, 7 });
    trie.insert(new int[] { 5, 6, 8 });
    trie.insert(new int[] { 5, 6, 7 });
    trie.insert(new int[] { 8, 6, 10 });
    trie.insert(new int[] { 5, 7, 11 });
    int[] seq = trie.getMostCommonSequenceStartingWith(5, 4);
    assertArrayEquals(new int[] {5, 6, 7}, seq);
  }

  @Test
  void producesMostCommonSequenceWithInvalidLength() {
    Trie trie = new Trie();
    trie.insert(new int[] { 5, 6, 7 });
    trie.insert(new int[] { 5, 6, 8 });
    trie.insert(new int[] { 5, 6, 7 });
    trie.insert(new int[] { 8, 6, 10 });
    trie.insert(new int[] { 5, 7, 11 });
    int[] seq = trie.getMostCommonSequenceStartingWith(5, 0);
    assertArrayEquals(new int[] {5}, seq);
  }

  @Test
  void producesMostCommonSequenceWithDeeperTrie() {
    Trie trie = new Trie();
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 9 });
    trie.insert(new int[] { 5, 6, 7, 8, 8, 8, 9 });
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 10 });
    trie.insert(new int[] { 5, 6, 8, 8, 8, 8, 9 });
    trie.insert(new int[] { 5, 6, 7, 7, 8, 8, 9 });
    trie.insert(new int[] { 6, 6, 7, 7, 8, 8, 9 });
    int[] seq = trie.getMostCommonSequenceStartingWith(5, 7);
    assertArrayEquals(new int[] {5, 6, 7, 7, 8, 8, 9}, seq);
  }

}
