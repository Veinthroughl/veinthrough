package veinthrough.leetcode.tree.trie;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [#Trie]: 字典树
 *
 * 1. 第212题，搜索多个单词
 * {@link veinthrough.leetcode.enumerate.backtrace.SearchWords#dfsTrie(List, int, int, Trie)}
 */
public class Trie {
    @Getter
    @Setter
    private String word = "";
    @Getter
    private Map<Character, Trie> children = new HashMap<>();

    public void insert(String word) {
        char[] chs = word.toCharArray();
        Trie current = this;
        for (char ch : chs) {
            current.children.putIfAbsent(ch, new Trie());
            current = current.children.get(ch);
        }
        current.word = word;            // terminal word
    }

    public Trie get(char ch) {
        return children.get(ch);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void remove(char ch) {
        children.remove(ch);
    }
}
