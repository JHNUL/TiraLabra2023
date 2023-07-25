package org.juhanir.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class TrieTest {

    private final String[] oneChild = { "E#4quarter" };
    private final String[] twoChildren = { "E#4quarter", "F#4quarter" };
    private final String[] threeChildren = { "E#4quarter", "F#4quarter", "A#4quarter" };
    private final String[] firstBranch = { "A", "B", "C" };
    private final String[] secondBranch = { "A", "B", "D" };
    private final String[] thirdBranch = { "A", "B", "E" };
    private final String[] fourthBranch = { "A", "B", "F" };
    private final String[] fifthBranch = { "A", "C", "F#" };

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
        last = trie.lookup(new String[] { "FOOBAR" });
        assertNull(last);
    }

    @Test
    void prefixSearchReturnsEmptyListWhenNotFound() {
        Trie trie = new Trie();
        trie.insert(threeChildren);
        var result = trie.prefixSearch(new String[] { "yi", "er", "san", "si" });
        assertInstanceOf(List.class, result);
        assertEquals(0, result.size());
    }

    @Test
    void prefixSearchReturnsEmptyListWhenPrefixEqualsKey() {
        Trie trie = new Trie();
        trie.insert(threeChildren);
        var result = trie.prefixSearch(threeChildren);
        assertInstanceOf(List.class, result);
        assertEquals(0, result.size());
    }

    @Test
    void prefixSearchReturnsEmptyListWhenPrefixEmpty() {
        Trie trie = new Trie();
        trie.insert(threeChildren);
        var result = trie.prefixSearch(new String[] {});
        assertInstanceOf(List.class, result);
        assertEquals(0, result.size());
    }

    @Test
    void prefixSearchReturnsImmediateChildrenWithOneNotePrefix() {
        Trie trie = new Trie();
        trie.insert(threeChildren);
        var result = trie.prefixSearch(new String[] { threeChildren[0] });
        assertEquals(1, result.size());
        assertEquals(result.get(0).getValue(), threeChildren[1]);
    }

    @Test
    void prefixSearchReturnsImmediateChildrenWithTwoNotePrefix() {
        Trie trie = new Trie();
        trie.insert(threeChildren);
        var result = trie.prefixSearch(new String[] { threeChildren[0], threeChildren[1] });
        assertEquals(1, result.size());
        assertEquals(result.get(0).getValue(), threeChildren[2]);
    }

    @Test
    void prefixSearchReturnsImmediateChildrenWhenExists() {
        Trie trie = new Trie();
        trie.insert(firstBranch);
        trie.insert(secondBranch);
        trie.insert(thirdBranch);
        trie.insert(fourthBranch);
        trie.insert(fifthBranch);
        var result = trie.prefixSearch(new String[] { "A", "B" });
        assertEquals(4, result.size());
        assertEquals("C", result.get(0).getValue());
        assertEquals("D", result.get(1).getValue());
        assertEquals("E", result.get(2).getValue());
        assertEquals("F", result.get(3).getValue());
    }

    @Test
    void prefixSearchReturnsImmediateChildrenNoDuplicates() {
        Trie trie = new Trie();
        trie.insert(firstBranch);
        trie.insert(secondBranch);
        trie.insert(thirdBranch);
        trie.insert(fourthBranch);
        trie.insert(fifthBranch);
        var result = trie.prefixSearch(new String[] { "A" });
        assertEquals(2, result.size());
        assertEquals("B", result.get(0).getValue());
        assertEquals("C", result.get(1).getValue());
    }
}
