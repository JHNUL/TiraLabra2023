package org.juhanir.domain;

import java.util.Collections;
import java.util.List;

public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode("");
    }

    /**
     * Inserts a note sequence to the Trie. Sequence
     * length determines the degree of the Markov Chain.
     * 
     * @param key sequence of note strings to save
     * @return number of insertions made
     */
    public int insert(String[] key) {
        TrieNode node = this.root;
        int insertions = 0;
        for (int i = 0; i < key.length; i++) {
            String note = key[i];
            if (!node.hasChild(note)) {
                node.addChild(key[i]);
                node.setIsLast(false);
                insertions++;
            }
            node = node.getChild(key[i]);
        }
        node.setIsLast(true);
        return insertions;
    }

    /**
     * Searches the Trie for the note sequence. Returns the final note
     * only if the whole sequence exists (there are no more notes after).
     * 
     * @param key sequence of note strings to search
     * @return TrieNode or null if sequence not found
     */
    public TrieNode lookup(String[] key) {
        TrieNode node = this.root;
        for (int i = 0; i < key.length; i++) {
            String note = key[i];
            if (!node.hasChild(note)) {
                return null;
            }
            node = node.getChild(key[i]);
        }
        if (node.getIsLast()) {
            return node;
        }
        return null;
    }

    /**
     * Searches the Trie for the melody sequence prefix, returns the child
     * nodes of the last node in the prefix.
     *
     * @param prefix sequence of note strings as prefix to the next note
     * @return List of TrieNodes (can be empty)
     */
    public List<TrieNode> prefixSearch(String[] prefix) {
        TrieNode node = this.root;
        if (prefix.length < 1)
            return Collections.emptyList();
        for (int i = 0; i < prefix.length; i++) {
            String note = prefix[i];
            if (!node.hasChild(note)) {
                return Collections.emptyList();
            }
            node = node.getChild(prefix[i]);
        }
        return node.getChildren();
    }

}
