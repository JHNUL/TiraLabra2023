package org.juhanir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TrieNodeTest {

  @Test
  void canInstantiate() {
    assertNotNull(new TrieNode(5));
  }

  @Test
  void canCreateNewTrieNodeWithValue() {
    TrieNode node = new TrieNode(5);
    assertEquals(5, node.getValue());
  }

  @Test
  void newTrieNodeHasNoChildren() {
    TrieNode node = new TrieNode(5);
    assertInstanceOf(TrieNode[].class, node.getChildren(), "Should be an array");
    assertFalse(node.hasChildren(), "Should have no children");
  }

  @Test
  void canAddChildren() {
    TrieNode node = new TrieNode(5);
    node.addChild(1);
    assertTrue(node.hasChildren());
    node.addChild(2);
    node.addChild(3);
    long kids = Arrays.stream(node.getChildren()).filter(Objects::nonNull).count();
    assertEquals(3, kids, "Should have three children");
  }

  @Test
  void doesNotAddDuplicates() {
    int testValue = 5;
    int childValue = 4;
    TrieNode node = new TrieNode(testValue);
    node.addChild(childValue);
    node.addChild(childValue);
    node.addChild(childValue);
    long kids = Arrays.stream(node.getChildren()).filter(Objects::nonNull).count();
    assertEquals(1, kids);
  }

  @Test
  void findsChildByValue() {
    int testValue = 5;
    int[] children = {1, 2, 3};
    TrieNode node = new TrieNode(testValue);
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
    }
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
      assertTrue(node.hasChild(children[i]), "Should find child");
    }
  }

  @Test
  void doesNotFindNonExistingChild() {
    int testValue = 5;
    int[] children = {1, 2, 3};
    TrieNode node = new TrieNode(testValue);
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
    }
    assertFalse(node.hasChild(testValue), "Should not find non-existing child");
    assertFalse(node.hasChild(35), "Should not find non-existing child");
  }

  @Test
  void returnsChildWhenFound() {
    int testValue = 5;
    int[] children = {1, 2, 3};
    TrieNode node = new TrieNode(testValue);
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
    }
    assertInstanceOf(TrieNode.class, node.getChild(children[1]), "Should be TrieNode");
    TrieNode kid = node.getChild(children[1]);
    assertEquals(children[1], kid.getValue(), "Value should be " + children[1]);
  }

  @Test
  void returnsNullWhenChildNotFound() {
    int testValue = 5;
    int[] children = {1, 2, 3};
    TrieNode node = new TrieNode(testValue);
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
    }
    assertNull(node.getChild(testValue), "Should be null");
  }

  @Test
  void returnsAllChildren() {
    int testValue = 5;
    int[] children = {1, 2, 3};
    TrieNode node = new TrieNode(testValue);
    for (int i = 0; i < children.length; i++) {
      node.addChild(children[i]);
    }
    List<TrieNode> kids =
        Arrays.stream(node.getChildren()).filter(Objects::nonNull).collect(Collectors.toList());
    assertEquals(3, kids.size());
    for (TrieNode trieNode : kids) {
      assertInstanceOf(TrieNode.class, trieNode);
    }
  }

  @Test
  void hasCountProperty() {
    TrieNode node = new TrieNode(5);
    assertEquals(0, node.getCount());
  }

  @Test
  void canIncrementCount() {
    TrieNode node = new TrieNode(5);
    node.incrementCount();
    node.incrementCount();
    node.incrementCount();
    assertEquals(3, node.getCount());
  }
}
