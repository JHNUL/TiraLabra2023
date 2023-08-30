package org.juhanir.domain;

import org.juhanir.Constants;

/**
 * A node in the trie. Represents a note.
 */
public class TrieNode {
  private TrieNode[] children;
  private int value;
  private int count;
  private int childCount;

  /**
   * Constructor for TrieNode.
   *
   * @param value the integer value of the note.
   */
  public TrieNode(int value) {
    this.value = value;
    this.children = new TrieNode[Constants.NOTE_ARRAY_SIZE];
    this.count = 0;
    this.childCount = 0;
  }

  public int getValue() {
    return this.value;
  }

  public int getCount() {
    return this.count;
  }

  public int getChildCount() {
    return this.childCount;
  }

  public void incrementCount() {
    this.count++;
  }

  public boolean hasChild(int value) {
    return this.children[value] != null;
  }

  public boolean hasChildren() {
    return this.childCount > 0;
  }

  public TrieNode getChild(int value) {
    return this.children[value];
  }

  /**
   * Adds a child to the node. Increments the count property of
   * the child if aalready exists. Also increment the childCount
   * property of the node for probability distribution calculation.
   *
   * @param value the numerical value of the child
   */
  public void addChild(int value) {
    if (this.children[value] == null) {
      this.children[value] = new TrieNode(value);
    }
    this.children[value].incrementCount();
    this.childCount++;
  }

  public TrieNode[] getChildren() {
    return this.children;
  }
}
