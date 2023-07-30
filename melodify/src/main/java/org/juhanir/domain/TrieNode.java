package org.juhanir.domain;

import java.util.Arrays;
import java.util.Objects;
import org.juhanir.utils.Constants;

public class TrieNode {
  private TrieNode[] children;
  private int value;
  private int count;

  public TrieNode(int value) {
    this.value = value;
    this.children = new TrieNode[Constants.NOTE_ARRAY_SIZE];
    for (int i = 0; i < Constants.NOTE_ARRAY_SIZE; i++) {
      this.children[i] = null;
    }
    this.count = 0;
  }

  public int getValue() {
    return this.value;
  }

  public int getCount() {
    return this.count;
  }

  public void incrementCount() {
    this.count++;
  }

  public boolean hasChild(int value) {
    return this.children[value] != null;
  }

  public boolean hasChildren() {
    return Arrays.stream(this.children).filter(Objects::nonNull).count() > 0;
  }

  public TrieNode getChild(int value) {
    return this.children[value];
  }

  public void addChild(int value) {
    this.children[value] = new TrieNode(value);
  }

  public TrieNode[] getChildren() {
    return this.children;
  }
}
