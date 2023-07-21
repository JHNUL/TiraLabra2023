package org.juhanir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TrieTest {

    private final String[] oneChild = { "E#4quarter" };
    private final String[] twoChildren = { "E#4quarter", "F#4quarter" };
    private final String[] threeChildren = { "E#4quarter", "F#4quarter", "A#4quarter" };

    @Test
    void canInstantiate() {
        assertNotNull(new Trie());
    }

    @Test
    void canInsertNode() {
        Trie trie = new Trie();
        int insertions = trie.insert(oneChild);
        assertEquals(1, insertions);
    }

    @Test
    void canInsertNodes() {
        Trie trie = new Trie();
        int insertions = trie.insert(threeChildren);
        assertEquals(3, insertions);
    }

    @Test
    void doesNotInsertDuplicates() {
        Trie trie = new Trie();
        int insertions = trie.insert(oneChild);
        assertEquals(1, insertions);
        insertions = trie.insert(twoChildren);
        assertEquals(1, insertions);
        insertions = trie.insert(threeChildren);
        assertEquals(1, insertions);
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
        TrieNode last = trie.lookup(twoChildren);
        assertNull(last);
        last = trie.lookup(oneChild);
        assertNull(last);
        last = trie.lookup(new String[]{"FOOBAR"});
        assertNull(last);
    }
}
