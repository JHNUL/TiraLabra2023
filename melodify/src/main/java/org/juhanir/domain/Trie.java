package org.juhanir.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import org.juhanir.Constants;

/**
 * Trie class.
 */
public class Trie {

  private TrieNode root;

  public Trie() {
    this.root = new TrieNode(Integer.MIN_VALUE);
  }

  /**
   * <p>Inserts a note sequence to the Trie. Sequence length follows the degree of the Markov Chain.
   * Increments the <code>childCount</code> property of the parent and the <code>count</code> property
   * of the added child.</p>
   *
   * @param key sequence of note strings to save
   */
  public void insert(int[] key) {
    TrieNode node = this.root;
    for (int i = 0; i < key.length; i++) {
      int numericNote = key[i];
      node.addChild(numericNote);
      node = node.getChild(numericNote);
    }
  }

  /**
   * <p>
   * Searches the Trie for the note sequence.
   * </p>
   * <p>
   * Returns the final note only if the whole sequence exists and there are no more notes after.
   * </p>
   *
   * @param key sequence of note strings to search
   * @return TrieNode or null if sequence not found
   */
  public TrieNode lookup(int[] key) {
    TrieNode node = this.root;
    for (int i = 0; i < key.length; i++) {
      int note = key[i];
      if (!node.hasChild(note)) {
        return null;
      }
      node = node.getChild(note);
    }
    return node;
  }

  /**
   * <p>
   * Searches the Trie for the melody sequence prefix and returns the child nodes of the last node
   * in the prefix.
   * </p>
   *
   * @param prefix sequence of note strings as prefix to the next note
   * @return Array of TrieNodes (can be empty)
   */
  public TrieNode[] prefixSearch(int[] prefix) {
    TrieNode node = this.root;
    for (int i = 0; i < prefix.length; i++) {
      int note = prefix[i];
      if (!node.hasChild(note)) {
        return new TrieNode[0];
      }
      node = node.getChild(prefix[i]);
    }
    return node.getChildren();
  }

  /**
   * Calculate the probabilities from a list of children.
   *
   * @return Array of probabilities
   */
  public double[] getProbabilities(TrieNode note) {
    int childCount = note != null ? note.getChildCount() : 0;
    TrieNode[] children = childCount > 0 ? note.getChildren() : new TrieNode[0];
    double[] probabilities = new double[Constants.NOTE_ARRAY_SIZE];
    double sum = 0.0;
    for (int j = 0; j < children.length; j++) {
      if (children[j] != null) {
        probabilities[j] = children[j].getCount() * 1.0 / childCount;
        sum += probabilities[j];
      }
    }
    // 0.0 for no children, otherwise check that probabilities sum up to one
    if (sum != 0.0 && Math.abs(1.0 - sum) > Constants.EPSILON) {
      throw new IllegalArgumentException("Probabilities must sum up to one " + Arrays.toString(probabilities));
    }
    return probabilities;
  }

  /**
   * Get the size of the whole trie.
   *
   * @return trie size
   */
  public int size() {
    return this.countNodes(this.root);
  }

  /**
   * Get a sequence of notes starting with the note given as argument and
   * the next note in the sequence is always the most common child.
   *
   * @param startingNote note that starts the sequence.
   * @param length length of the sequence.
   * @return sequence
   */
  public int[] getMostCommonSequenceStartingWith(int startingNote, int length) {
    if (length <= 1) {
      return new int[] { startingNote };
    }
    int[] sequence = new int[length];
    sequence[0] = startingNote;
    TrieNode node = this.root.getChildren()[startingNote];
    for (int i = 1; i < length; i++) {
      TrieNode[] children = Arrays.stream(node.getChildren()).filter(Objects::nonNull).toArray(TrieNode[]::new);
      TrieNode selected = Arrays.stream(children).max(Comparator.comparingInt(TrieNode::getCount)).orElse(null);
      if (selected == null) {
        return Arrays.copyOfRange(sequence, 0, i);
      }
      sequence[i] = selected.getValue();
      node = selected;
    }
    return sequence;
  }

  /**
   * Clear the existing trie by resetting the root node.
   */
  public void clear() {
    this.root = new TrieNode(Integer.MIN_VALUE);
  }

  private int countNodes(TrieNode node) {
    int c = 1;
    if (!node.hasChildren()) {
      return c;
    }
    for (TrieNode child : node.getChildren()) {
      if (child != null) {
        c += this.countNodes(child);
      }
    }
    return c;
  }

}
