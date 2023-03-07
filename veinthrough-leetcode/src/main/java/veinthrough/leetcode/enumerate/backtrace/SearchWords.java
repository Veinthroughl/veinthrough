package veinthrough.leetcode.enumerate.backtrace;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;
import veinthrough.api.generic.Tuple;
import veinthrough.leetcode.tree.trie.Trie;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.array.Array.copy;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第79题: 搜索单个单词
 * 给定一个 m x n 二维字符网格 board 和一个字符串单词 word 。如果 word 存在于网格中，返回 true ；否则，返回 false 。
 * 单词必须按照字母顺序，通过相邻的单元格内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。
 * 同一个单元格内的字母不允许被重复使用。
 *
 * 示例：
 * board = {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}}
 * 输入：word = 'ABCCED', 输出：true
 * 输入：word = 'SEE', 输出：true
 * 输入：word = 'ABCB', 输出：false
 *
 * 第212题：搜索多个单词
 * 给定一个 m x n 二维字符网格 board 和一个单词（字符串）列表 words， 返回所有二维网格上的单词 。
 * 单词必须按照字母顺序，通过 相邻的单元格 内的字母构成，其中“相邻”单元格是那些水平相邻或垂直相邻的单元格。
 * 同一个单元格内的字母在一个单词中不允许被重复使用。
 */
@Slf4j
public class SearchWords {
    private final char[][] data1 = new char[][]{
            {'a', 'b', 'c', 'e'},
            {'s', 'f', 'c', 's'},
            {'a', 'd', 'e', 'e'}};
    private final char[][] data2 = new char[][]{
            {'o', 'a', 'a', 'n'},
            {'e', 't', 'a', 'e'},
            {'i', 'h', 'k', 'r'},
            {'i', 'f', 'l', 'v'}};
    private final String[] words1 = {"abcced", "see", "abcb", "eat"};
    private final String[] words2 = {"oath", "pea", "eat", "rain", "see"};
    private char[][] board;
    private char[] chs;
    private int rows, columns;
    private int n;

    @Test
    public void test79() {
        Stream.of(
                Tuple.of(new char[][]{{'a'}}, "a"), // true
                Tuple.of(copy(data1), "abcced"), // true
                Tuple.of(copy(data1), "see"), // true
                Tuple.of(copy(data1), "abcb")) // false
                .forEach(tuple -> log.info(methodLog(
                        "" + searchWord(tuple.getFirst(), tuple.getSecond()))));
    }

    @Test
    public void test212() {
        Stream.of(words1, words2)
                .forEach(words -> Stream.of(copy(data1), copy(data2))
                        .forEach(board -> log.info(methodLog(
                                "words", Arrays.toString(words),
                                "board", Array.stringOf2DArray(board),
                                "matched", Arrays.toString(searchWords(board, words))))));
    }

    /**
     * 搜索多个单词:
     * (1) 搜索单个单词, 一边dfs board一边与单词匹配
     * (2) 搜索多个单词, 一边dfs board一边与字典树匹配, 避免了每一步都去匹配多个单词
     */
    private String[] searchWords(char[][] board, String[] words) {
        // boundary

        this.board = board;
        rows = board.length;
        columns = board[0].length;

        // 1. build trie
        Trie trie = new Trie();
        for (String word : words) {
            trie.insert(word);
        }

        // 2. search board
        List<String> res = new LinkedList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                dfsTrie(res, i, j, trie);
            }
        }
        return res.toArray(new String[0]);
    }

    /**
     * 在now的子节点中找匹配board[i][j]
     */
    public void dfsTrie(List<String> matched, int i, int j, Trie now) {
        // 1. now的所有子结点都不匹配board[i][j]
        char ch = board[i][j];
        Trie child;
        if ((child = now.get(ch)) == null) return;

        // 2. 在now中找到匹配board[i][j]的子节点
        // 且该子节点有一个(word)
        if (!child.getWord().equals("")) {
            matched.add(child.getWord());
            // 避免重复搜索: 删除child的word(置空),
            // 删除word之后：
            // 2.(1) 如果child为非叶子节点, 在board其他地方还可以作为一个【中间节点(不包含word)】往下匹配，
            //   但是如果board其他地方匹配word, 也不会再添加word了
            child.setWord("");
            // 2.(2) 如果child为叶子节点，其实可以把child从now的children中直接删除
            if (child.isEmpty()) {
                now.remove(ch);
                return;
            }
        }
        // 3. dfs子节点(child)
        // 子节点(child)为一个非叶子节点, 才需要再往下遍历了
        board[i][j] = '#';
        for (int[] neighbor : neighbors(i, j)) {
            dfsTrie(matched, neighbor[0], neighbor[1], child);
        }
        board[i][j] = ch;
    }

    /**
     * 搜索单个单词
     */
    private boolean searchWord(char[][] board, String word) {
        // boundary

        //
        this.board = board;
        rows = board.length;
        columns = board[0].length;
        chs = word.toCharArray();
        n = word.length();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == chs[0] && dfs(i, j, 0))
                    return true;
            }
        }
        return false;
    }

    public boolean dfs(int i, int j, int k) {
        // 1. end
        if (k == n) return true;

        char ch = board[i][j];
        // 2. 字符不同直接返回false
        if (ch != chs[k]) return false;

        // 3. 字符相同
        // 如果n==1， 那么将没有neighbor，不会进入循环，所以应该直接返回true
        // 测试用例<{{'a'}},"a">
        if (n == 1) return true;
        // 为了避免形成环, 可以有两种方法
        // (1) 可以使用visited[][]
        // (2) 将该趟走过的单元置一个特殊的字符'#'
        board[i][j] = '#';
        for (int[] neighbor : neighbors(i, j))
            if (dfs(neighbor[0], neighbor[1], k + 1))
                return true;
        board[i][j] = ch;
        return false;
    }

    private int[][] neighbors(int i, int j) {
        int[][] neighbors = new int[4][2];
        int count = 0;

        int up = i - 1, down = i + 1, left = j - 1, right = j + 1;
        if (up >= 0) neighbors[count++] = new int[]{up, j};
        if (down < rows) neighbors[count++] = new int[]{down, j};
        if (left >= 0) neighbors[count++] = new int[]{i, left};
        if (right < columns) neighbors[count++] = new int[]{i, right};
        return Arrays.copyOfRange(neighbors, 0, count);
    }
}
