package org.juhanir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TrieNodeTest {

    @Test
    void canInstantiate() {
        assertNotNull(new TrieNode(""));
    }

    @Test
    void canCreateNewTrieNodeWithEmptyString() {
        TrieNode node = new TrieNode("");
        assertEquals("", node.getValue());
    }

    @Test
    void canCreateNewTrieNodeWithValue() {
        String testValue = "E#4quarter";
        TrieNode node = new TrieNode(testValue);
        assertEquals(testValue, node.getValue());
    }

    @Test
    void newTrieNodeHasNoChildren() {
        String testValue = "E#4quarter";
        TrieNode node = new TrieNode(testValue);
        assertInstanceOf(List.class, node.getChildren(), "Should be a list");
        assertEquals(0, node.getChildren().size(), "Should have no children");
    }

    @Test
    void canAddChildren() {
        String testValue = "E#4quarter";
        TrieNode node = new TrieNode(testValue);
        int size = node.addChild("G#4quarter");
        assertEquals(1, size, "Should have one child");
        size = node.addChild("F#4quarter");
        assertEquals(2, size, "Should have two children");
        size = node.addChild("A#4quarter");
        assertEquals(3, size, "Should have three children");
    }

    @Test
    void doesNotAddDuplicates() {
        String testValue = "E#4quarter";
        String childValue = "G#4quarter";
        TrieNode node = new TrieNode(testValue);
        int size = node.addChild(childValue);
        assertEquals(1, size);
        size = node.addChild(childValue);
        assertEquals(1, size);
        size = node.addChild(childValue);
        assertEquals(1, size);
    }

    @Test
    void findsChildByValue() {
        String testValue = "E#4quarter";
        String[] children = { "G#4quarter", "F#4quarter", "A#4quarter" };
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
        String testValue = "E#4quarter";
        String[] children = { "G#4quarter", "F#4quarter", "A#4quarter" };
        TrieNode node = new TrieNode(testValue);
        for (int i = 0; i < children.length; i++) {
            node.addChild(children[i]);
        }
        assertFalse(node.hasChild(testValue), "Should not find non-existing child");
        assertFalse(node.hasChild("FooBar"), "Should not find non-existing child");
    }

    @Test
    void returnsChildWhenFound() {
        String testValue = "E#4quarter";
        String[] children = { "G#4quarter", "F#4quarter", "A#4quarter" };
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
        String testValue = "E#4quarter";
        String[] children = { "G#4quarter", "F#4quarter", "A#4quarter" };
        TrieNode node = new TrieNode(testValue);
        for (int i = 0; i < children.length; i++) {
            node.addChild(children[i]);
        }
        assertNull(node.getChild(testValue), "Should be null");
    }

    @Test
    void returnsAllChildren() {
        String testValue = "E#4quarter";
        String[] children = { "G#4quarter", "F#4quarter", "A#4quarter" };
        TrieNode node = new TrieNode(testValue);
        for (int i = 0; i < children.length; i++) {
            node.addChild(children[i]);
        }
        List<TrieNode> kids = node.getChildren();
        assertEquals(3, kids.size());
        for (TrieNode trieNode : kids) {
            assertInstanceOf(TrieNode.class, trieNode);
        }
    }

    @Test
    void hasCountProperty() {
        String testValue = "E#4quarter";
        TrieNode node = new TrieNode(testValue);
        assertEquals(0, node.getCount());
    }

    @Test
    void canIncrementCount() {
        String testValue = "E#4quarter";
        TrieNode node = new TrieNode(testValue);
        node.incrementCount();
        node.incrementCount();
        node.incrementCount();
        assertEquals(3, node.getCount());
    }
}
