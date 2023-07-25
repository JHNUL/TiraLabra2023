package org.juhanir.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode("");
    }

    /**
     * Inserts a note sequence to the Trie. Sequence
     * length determines the degree of the Markov Chain.
     * 
     * Increments each nodes 'count' property when inserting
     * it as child.
     *
     * @param key sequence of note strings to save
     */
    public void insert(String[] key) {
        TrieNode node = this.root;
        for (int i = 0; i < key.length; i++) {
            String note = key[i];
            if (!node.hasChild(note)) {
                node.addChild(key[i]);
                node.setIsLast(false);
            }
            node = node.getChild(key[i]);
            node.incrementCount();
        }
        node.setIsLast(true);
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
        for (int i = 0; i < prefix.length; i++) {
            String note = prefix[i];
            if (!node.hasChild(note)) {
                return Collections.emptyList();
            }
            node = node.getChild(prefix[i]);
        }
        return node.getChildren();
    }

    /**
     * Calculate the probabilities from a list of children.
     *
     * @return Map where note value is key and probability is value.
     */
    public Map<String, Double> getProbabilities(List<TrieNode> children) {
        int childCount = children.stream().map(TrieNode::getCount).reduce(0, Integer::sum);
        Map<String, Double> probabilities = new HashMap<>(childCount);
        for (TrieNode child : children) {
            probabilities.put(child.getValue(), (double) child.getCount() / childCount);
        }
        return probabilities;
    }

    /**
     * Get the size of the whole tree
     *
     * @return int size
     */
    public int size() {
        return this.countNodes(this.root);
    }

    private int countNodes(TrieNode node) {
        int c = 1;
        if (node.getChildren().isEmpty())
            return c;
        for (TrieNode child : node.getChildren())
            c += this.countNodes(child);
        return c;
    }

}
