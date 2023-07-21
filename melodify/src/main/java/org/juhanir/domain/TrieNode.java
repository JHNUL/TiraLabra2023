package org.juhanir.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrieNode {
    private Map<String, TrieNode> children;
    private final String value;
    private boolean isLast;

    public TrieNode(String value) {
        this.value = value;
        this.children = new HashMap<>();
        this.isLast = false;
    }

    public String getValue() {
        return this.value;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public boolean getIsLast() {
        return this.isLast;
    }

    public boolean hasChild(String value) {
        return this.children.containsKey(value);
    }

    public TrieNode getChild(String value) {
        return this.children.getOrDefault(value, null);
    }

    public int addChild(String value) {
        this.children.put(value, new TrieNode(value));
        return this.children.size();
    }

    public List<TrieNode> getChildren() {
        return this.children.values().stream().collect(Collectors.toList());
    }
}
