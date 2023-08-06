package org.juhanir.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import org.juhanir.utils.Constants;

/**
 * Trie class.
 */
public class Trie {

  private TrieNode root;

  public Trie() {
    this.root = new TrieNode(Integer.MIN_VALUE);
  }

  /**
   * Inserts a note sequence to the Trie. Sequence length determines the degree of the Markov Chain.
   * Increments each nodes 'count' property when inserting it as child.
   *
   * @param key sequence of note strings to save
   */
  public void insert(int[] key) {
    TrieNode node = this.root;
    for (int i = 0; i < key.length; i++) {
      int note = key[i];
      if (!node.hasChild(note)) {
        node.addChild(key[i]);
      }
      node = node.getChild(key[i]);
      node.incrementCount();
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
      node = node.getChild(key[i]);
    }
    if (!node.hasChildren()) {
      return node;
    }
    return null;
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
  public double[] getProbabilities(TrieNode[] children) {
    int childCount =
        Arrays.stream(children).filter(Objects::nonNull).mapToInt(TrieNode::getCount).sum();
    double[] probabilities = new double[Constants.NOTE_ARRAY_SIZE];
    for (int j = 0; j < children.length; j++) {
      if (children[j] != null) {
        probabilities[j] = children[j].getCount() * 1.0 / childCount;
      }
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
   * Get a random sequence of the argument length.
   *
   * @param length length of the sequence.
   * @return sequence
   */
  public int[] getRandomSequence(int length) {
    if (length <= 0) {
      return new int[] {};
    }
    TrieNode node = this.root;
    int[] sequence = new int[length];
    Random rand = new Random();
    for (int i = 0; i < length; i++) {
      TrieNode[] children =
          Arrays.stream(node.getChildren()).filter(Objects::nonNull).toArray(TrieNode[]::new);
      if (children.length == 0) {
        return sequence;
      }
      TrieNode selected = children[rand.nextInt(children.length)];
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
