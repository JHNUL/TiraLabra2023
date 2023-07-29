package org.juhanir.domain;

import java.util.Arrays;
import java.util.Objects;

import org.juhanir.utils.Constants;

public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode(Integer.MIN_VALUE);
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
     * Searches the Trie for the note sequence. Returns the final note
     * only if the whole sequence exists (there are no more notes after).
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
     * Searches the Trie for the melody sequence prefix, returns the child
     * nodes of the last node in the prefix.
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
        int childCount = Arrays.stream(children)
                .filter(Objects::nonNull)
                .map(TrieNode::getCount)
                .reduce(0, Integer::sum);
        double[] probabilities = new double[Constants.NOTE_ARRAY_SIZE];
        for (int j = 0; j < children.length; j++) {
            if (children[j] != null) {
                probabilities[j] = (double) children[j].getCount() / childCount;
            }
        }
        return probabilities;
    }

    /**
     * Get the size of the whole trie
     *
     * @return trie size
     */
    public int size() {
        return this.countNodes(this.root);
    }

    private int countNodes(TrieNode node) {
        int c = 1;
        if (!node.hasChildren())
            return c;
        for (TrieNode child : node.getChildren()) {
            if (child != null) {
                c += this.countNodes(child);
            }
        }
        return c;
    }

}
