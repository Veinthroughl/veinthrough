package veinthrough.leetcode.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.generic.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第68题：文本左右对齐，
 * 给定一个单词数组words和一个长度maxWidth，重新排版单词，使其成为每行恰好有maxWidth个字符，且左右两端对齐的文本。
 * 你应该使用“贪心算法”来放置给定的单词；也就是说，尽可能多地往每行中放置单词。必要时可用空格' '填充，使得每行恰好有 maxWidth个字符。
 * 要求尽可能均匀分配单词间的空格数量。如果某一行单词间的空格不能均匀分配，则左侧放置的空格数要多于右侧的空格数。
 * 文本的最后一行应为左对齐，且单词之间不插入额外的空格。
 * 注意:
 * 单词是指由非空格字符组成的字符序列。
 * 每个单词的长度大于0，小于等于maxWidth。
 * 输入单词数组words至少包含一个单词。
 */
@Slf4j
public class JustifyString {
    @Test
    public void test68() {
        Stream.of(
                Tuple.of(new String[]{"Listen", "to", "many,", "speak", "to", "a", "few."}, 6),
                Tuple.of(new String[]{"Science", "is", "what", "we", "understand", "well", "enough",
                        "to", "explain", "to", "a", "computer.", "Art", "is", "everything", "else", "we", "do"}, 20),
                Tuple.of(new String[]{"ask", "not", "what", "your", "country", "can", "do", "for",
                        "you", "ask", "what", "you", "can", "do", "for", "your", "country"}, 16),
                Tuple.of(new String[]{"What", "must", "be", "acknowledgment", "shall", "be"}, 16),
                Tuple.of(new String[]{"This", "is", "an", "example", "of", "text", "justification."}, 16),
                Tuple.of(new String[]{"Science", "is", "what", "we", "understand", "well", "enough", "to",
                        "explain", "to", "a", "computer.", "Art", "is", "everything", "else", "we", "do"}, 20))
                .forEach(tuple -> log.info(methodLog(
                        String.format("Justify with %d %s", tuple.getSecond(), Arrays.toString(tuple.getFirst())),
                        "" + fullJustify(tuple.getFirst(), tuple.getSecond()))));
    }


    private String[] words;
    private int[] lens;
    private int maxWidth;
    private String[] spaces;

    private List<String> fullJustify(String[] words, int maxWidth) {
        //boundary

        // 1. init
        this.words = words;
        this.maxWidth = maxWidth;
        int n = words.length;

        // 2. lens
        lens = Stream.of(words)
                .mapToInt(String::length)
                .toArray();

        // 3. spaces
        // 提前准备1,2,3...个空格的字符串
        spaces = new String[maxWidth - 1];
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < maxWidth - 1; i++)
            spaces[i] = str.append(' ').toString();

        // 4. calculate
        int start = 0;
        int width = lens[0];
        int sum;
        List<String> res = new ArrayList<>(n >> 1);
        for (int i = 1; i < n; i++) {
            sum = width + lens[i] + 1;
            // 4.1 下一个作为下一行
            if (sum == maxWidth) {
                // (1) 当前行, 有可能是最后一行
                res.add(i == n - 1 ? jointLast(start, i) : joint(start, i));
                // (2) 下一行
                // 这里即使i==n-1(也就是i+1==n)也需要更新start，
                // 否则会导致循环外还会计算一遍
                if ((start = i + 1) < n) width = lens[i + 1];
                i++;
                // 4.2 当前行作为下一行
            } else if (sum > maxWidth) {
                // 当前行
                res.add(joint(start, i - 1));
                // 下一行
                width = lens[(start = i)]; // 下一行
                // 4.3 继续处理当前行
            } else {
                width = sum;
            }
        }
        // 最后一行有可能没有处理
        if (start <= n - 1) res.add(jointLast(start, n - 1));
        return res;
    }

    private String joint(int from, int to) {
        StringBuilder str = new StringBuilder();
        // 1 word
        if (from == to) {
            str.append(words[from]);
            if (lens[from] < maxWidth) str.append(spaces[maxWidth - lens[from] - 1]);
            return str.toString();
        }
        // 2 words
        if (to == from + 1) {
            str.append(words[from]);
            if (lens[from] + lens[to] < maxWidth) str.append(spaces[maxWidth - lens[from] - lens[to] - 1]);
            str.append(words[to]);
            return str.toString();
        }
        // >2 words
        // (1)
        int wordsLen = 0;
        for (int i = from; i <= to; i++) wordsLen += lens[i];
        // (2)
        int gaps = to - from;
        int average = (maxWidth - wordsLen) / gaps;
        String averageSpaces = spaces[average - 1];
        String moreSpaces = spaces[average];
        // [from, cut]需要多填充一个空格
        // (cut,to]填充average个空格
        int cut = (maxWidth - wordsLen) % gaps + from;
        // (3)
        str.append(words[from]);
        for (int i = from + 1; i <= to; i++)
            if (i <= cut) str.append(moreSpaces).append(words[i]);
            else str.append(averageSpaces).append(words[i]);
        return str.toString();
    }

    private String jointLast(int from, int to) {
        StringBuilder str = new StringBuilder();
        str.append(words[from]);
        int wordsLen = lens[from];
        for (int i = from + 1; i <= to; i++) {
            str.append(' ').append(words[i]);
            wordsLen = wordsLen + 1 + lens[i];
        }
        if (wordsLen < maxWidth) str.append(spaces[maxWidth - wordsLen - 1]);
        return str.toString();
    }
}
