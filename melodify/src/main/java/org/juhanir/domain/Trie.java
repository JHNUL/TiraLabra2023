package org.juhanir.domain;

import java.util.List;

public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode("");
    }

    /**
     * Inserts a chord sequence to the Trie. Sequence
     * length determines the degree of the Markov Chain.
     * 
     * @param key sequence of chord strings to save
     * @return number of insertions made
     */
    public int insert(String[] key) {
        TrieNode node = this.root;
        int insertions = 0;
        for (int i = 0; i < key.length; i++) {
            String note = key[i];
            if (!node.hasChild(note)) {
                node.addChild(key[i]);
                insertions++;
            }
            node = node.getChild(key[i]);
        }
        node.setIsLast(true);
        return insertions;
    }

    /**
     * Searches the Trie for the chord sequence. Returns the final note
     * only if the whole sequence exists (there are no more notes after).
     * 
     * @param key sequence of chord strings to search
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

    public List<TrieNode> prefixSearch(String[] prefix) {
        // TODO: implement
        return null;
    }

}
