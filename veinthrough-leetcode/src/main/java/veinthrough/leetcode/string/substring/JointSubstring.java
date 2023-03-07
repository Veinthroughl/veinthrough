package veinthrough.leetcode.string.substring;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.string.KMP;
import veinthrough.leetcode.list.KthFromEnd;
import veinthrough.leetcode.list.MergeLists;
import veinthrough.leetcode.list.api.ListNode;

import java.util.*;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.leetcode.list.KthFromEnd.kthFromEnd;
import static veinthrough.leetcode.list.api.ListNode.of;

/**
 * 第30题, 串联所有单词的子串:
 * 给定一个字符串s和一些【长度相同】的单词words。找出s中恰好可以由words中所有单词串联形成的子串的起始位置。
 * 注意子串要与words中的单词完全匹配，中间不能有其他字符，但不需要考虑words中单词串联的顺序。
 * 类似于{@link LongestSubstring#minWindow(String, String)}中是要覆盖所有【chars】,这里是要覆盖所有【words】
 *
 * 双指针(滑动窗口)
 * (1) {@link LongestSubstring#minWindow(String, String)}
 * (2) {@link LongestSubstring#longestSubstringWithoutRepetition2(String)}
 * (3) {@link JointSubstring}
 */
@SuppressWarnings("ConstantConditions")
@Slf4j
public class JointSubstring {
    private static int STR_MAX_LEN = 1000;
    private int wordLen;
    private List<Integer> founds;
    private Map<ListNode, Boolean> rounds;

    @Test
    public void test30() {
        // result:
        // [0,3,12]
        // []
        // [0,4,12]
        Stream.of(
                jointSubstring_("barfoobarthefoobarman", new String[]{"foo", "bar"}),
                // 0123456789 10 11 12 13 14 15 16 17 18 19 20 21 22 22
                // wordgoodgo  o  d  g  o  o  d  b  e  s  t  w  o  r  d
                jointSubstring_("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "word"}),
                jointSubstring_("wordgoodbestwordwordbestgood", new String[]{"word", "good", "best", "word"})
        )
                .forEach(result ->
                        log.info(methodLog(result.toString())));

    }

    @Test
    public void test30_2() {
        Stream.of(
                jointSubstring2("barfoobarthefoobarman", new String[]{"foo", "bar"}),
                // 0123456789 10 11 12 13 14 15 16 17 18 19 20 21 22 22
                // wordgoodgo  o  d  g  o  o  d  b  e  s  t  w  o  r  d
                jointSubstring2("wordgoodgoodgoodbestword", new String[]{"word", "good", "best", "word"}),
                jointSubstring2("wordgoodbestwordwordbestgood", new String[]{"word", "good", "best", "word"}))
                .forEach(result ->
                        log.info(methodLog(result.toString())));

    }

    /**
     * 方法1(自己)：
     * 1. 通过KMP查找出每个word在s出现的所有index: List<Integer>
     * 那么所有word就为一个List<List<Integer>>  -->  List[], List[]中每个List为每个word在s出现的所有index
     * 2. 将List[]按出现的index做成一个链ListNode({@link MergeLists})
     * 3. 通过每个round(以某个index开始)连续的匹配所有需要匹配的word，所有需要匹配的word放在_wordsUnvisited
     * 每+1的index都为一个round, 但是一个round可能同时走完几个round, 所以需要记录每个round是否已经走过，
     * 而且从倒数第wordLen(word总个数, {@link KthFromEnd})开始往后已经不可能成为round，
     * 因为后面的word数不够
     * 匹配过程：
     * (1) 因为word是等长的(假设为word_len), index每次+word_len
     * (2) 不断地在链上拿下一个word，链上的word的wordIndex可能在[_index-word_len,_index+word_len]这个区间,
     * > 匹配单个word成功：
     * 在这个区间找到(==index)的word，_wordsUnvisited中去掉匹配的word并进行下一个word的匹配
     * > 匹配单个word失败：
     * 拿下来的word的wordIndex直到>index没有找到==index的word，宣告该round失败
     * > 全部匹配成功：
     * wordsUnvisited为空说明完全匹配成功，
     * 匹配成功一个，很容易去找到另一个匹配：只需要将匹配成功去掉头部(head), 然后在后面(+wordIndex)匹配head的word
     *
     * 0123456789 10 11 12 13 14 15 16 17 18 19 20
     * barfoobart  h  e  f  o  o  b  a  r  m  a  n
     */
    private List<Integer> jointSubstring2(String s, String[] _words) {
        this.wordLen = _words[0].length();
        int wordsLen = _words.length;
        this.founds = new LinkedList<>();

        // _words中可能有重复的元素
        Multiset<String> words = HashMultiset.create(Lists.newArrayList(_words));
        Multiset<Integer> unvisited = HashMultiset.create();

        // 1. find all occurrences of all words
        // edit: LinkedList/ visit: ArrayList
        List<List<Integer>> occurrences = new LinkedList<>();
        Map<Integer, Integer> wordIndex = new HashMap<>();
        int i = 0;
        for (String word : words.elementSet()) {
            // Variable used in lambda expression should be final or effectively final
            occurrences.add(new ArrayList<>(KMP.searchAll(s, word)));
            unvisited.add(i, words.count(word));
            for (Integer occurrence : occurrences.get(i)) wordIndex.put(occurrence, i);
            i++;
        }

        // 2. merge all occurrences by occurrence index
        ListNode[] occurrenceNodes = occurrences.stream()
                .map(list ->
                        of(list.stream()
                                .mapToInt(Integer::intValue)
                                .toArray()))
                .toArray(ListNode[]::new);
        ListNode mergedHead = MergeLists.mergeLists(occurrenceNodes);
        // 为了同时包含第几个list和index的数据
        // 第i个word，出现在s的occurrence位置
        ListNode p = mergedHead;
        while (p != null) {
            p.setVal(p.getVal() + wordIndex.get(p.getVal()) * STR_MAX_LEN);
            p = p.getNext();
        }

        // 3. 构造rounds
        // 倒数第wordsLen个
        ListNode nthFromEnd = kthFromEnd(mergedHead, wordsLen).getSecond();
        ListNode node = mergedHead;
        // 直到倒数第wordLen个, 否则剩下的个数不需要再来一个round
        rounds = new LinkedHashMap<>();
        while (node != nthFromEnd) {
            rounds.put(node, true);
            node = node.getNext();
        }
        rounds.put(node, true);

        // 4. 执行rounds
        // 执行round的过程中，如果匹配一个，可以通过去掉该匹配的头来很方便的开启下一个round
        ListNode head;
        for (Map.Entry<ListNode, Boolean> round : rounds.entrySet()) {
            head = round.getKey();
            if (round.getValue()) {
                rounds.put(head, false); // visited
                _round(head, head,
                        head.getVal() % STR_MAX_LEN,
                        HashMultiset.create(unvisited));
            }
        }
        return founds;
    }

    private void _round(ListNode _head, ListNode _word, int _index, Multiset<Integer> _wordsUnvisited) {
        int word, wordIndex;
        word = _word.getVal() / STR_MAX_LEN;
        wordIndex = _word.getVal() % STR_MAX_LEN;

        // 1. matched
        if (_wordsUnvisited.contains(word) && _index == wordIndex) {
            // visited
            _wordsUnvisited.remove(word, 1);
            // 1.(1) found one
            // 执行round的过程中，如果匹配一个，可以通过去掉该匹配的头来很方便的开启下一个round
            if (_wordsUnvisited.size() == 0) {
                founds.add(_head.getVal() % STR_MAX_LEN); // add founds

                // get head of next round
                ListNode nextHead = _head.getNext();
                int headWord = _head.getVal() / STR_MAX_LEN;
                int headIndex = _head.getVal() % STR_MAX_LEN;
                int nextHeadIndex = nextHead.getVal() % STR_MAX_LEN;
                while (nextHead != null && nextHeadIndex != headIndex + wordLen) // index间隔wordLen
                    nextHead = nextHead.getNext();
                // next round, may have been visited
                if (rounds.get(nextHead) != null && rounds.get(nextHead)) {
                    // 去掉了匹配的头，该round中只需要在后面额外找一个和当前的word匹配
                    rounds.put(nextHead, false); // visited
                    _round(nextHead, _word.getNext(),
                            wordIndex + wordLen, // index间隔wordLen
                            HashMultiset.create(Collections.singletonList(headWord)));
                }
            }
            // 1.(2) continue this round
            else {
                _round(_head, _word.getNext(), _index + wordLen, _wordsUnvisited); // index间隔wordLen
            }
        }
        // 2.(1) unmatched, next word of this round
        else if (wordIndex < _index) {
            _round(_head, _word.getNext(), _index, _wordsUnvisited);
            // stop this round, next round
        }
        // 2.(2) unmatched, return
    }

    /**
     * 方法2(题解)：map+双指针(滑动窗口)
     *
     * 类似于{@link LongestSubstring#minWindow(String, String)}中是要覆盖所有chars,这里是要覆盖所有words
     */
    @SuppressWarnings({"Duplicates", "unused"})
    private List<Integer> jointSubstring(String s, String[] words) {
        List<Integer> res = new LinkedList<>();
        if (s == null || s.length() == 0 || words == null || words.length == 0) return res;
        HashMap<String, Integer> map = new HashMap<>();
        int oneWord = words[0].length();
        int wordNum = words.length;
        // 1. 获取每个word的数量
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        // 2. 只需要one_word轮，因为每个单词长度相同
        for (int i = 0; i < oneWord; i++) {
            int left = i, right = i, count = 0;
            HashMap<String, Integer> tmpMap = new HashMap<>();
            // (1) right每次右移one_word，试图去匹配一个单词
            while ((right += oneWord) <= s.length()) {
                String w = s.substring(right, right + oneWord);
                // (2) 找到不匹配的单词, 清空left/right
                if (!map.containsKey(w)) {
                    count = 0;
                    left = right;
                    tmpMap.clear();
                    // (3) 找到匹配的单词
                } else {
                    // 将该单词次数+1， 总次数+1
                    // 后面的while保证了每个单词都不会超出map中的次数
                    // 只有当前添加的单词可能超出次数
                    tmpMap.merge(w, 1, (oldValue, newValue) -> oldValue + newValue);
                    count++;
                    // w/t_w
                    // w: 超过次数的单词
                    // t_w: left不断右移直到找到匹配w(超过次数)的单词，count--
                    // 这里保证了每个单词都不会超出map中的次数
                    while (tmpMap.getOrDefault(w, 0) > map.getOrDefault(w, 0)) {
                        String t_w = s.substring(left, left + oneWord);
                        count--;
                        tmpMap.merge(t_w, -1, (oldValue, newValue) -> oldValue + newValue);
                        left += oneWord;
                    }
                    // 找到整个匹配，因为保证了每个单词都不会超出map中的次数
                    if (count == wordNum) res.add(left);
                }
            }
        }
        return res;
    }

    /**
     * 方法2(题解)：map+双指针，但是其中使用Guava中的MultiSet来实现
     *
     * @see veinthrough.leetcode.string.substring.LongestSubstring#longestSubstringWithoutRepetition2(String) (String)
     */
    @SuppressWarnings("Duplicates")
    private List<Integer> jointSubstring_(String s, String[] words) {
        List<Integer> res = new LinkedList<>();
        int len, neededWordsNum;
        // 特殊情况
        if (s == null || (len = s.length()) == 0 || words == null || (neededWordsNum = words.length) == 0) return res;

        // 非特殊情况
        int oneWord = words[0].length();
        // 1. 通过Multiset构造neededWords
        Multiset<String> neededWords = HashMultiset.create();
        int neededWordsLen = oneWord * neededWordsNum;
        neededWords.addAll(Lists.newArrayList(words));
        int left, right, count;
        String word, putWord;
        Multiset<String> putWords = HashMultiset.create();
        // 2. 通过双指针来构造一个匹配区间[left,right)
        // 只需要one_word轮，因为每个单词长度相同
        for (int i = 0; i < oneWord; i++) {
            left = i;
            right = i;
            count = 0;
            // left+neededWordsLen<=len: 剩下的长度足够匹配所有单词
            // right+oneWord<=len: 剩下的长度足够匹配一个单词
            while (left + neededWordsLen <= len && right + oneWord <= len) {
                word = s.substring(right, right = right + oneWord);
                // 2.(1) 遇到不需要(不匹配)的word
                if (!neededWords.contains(word)) {
                    count = 0;
                    left = right;
                    putWords.clear();
                } else {
                    count++;
                    putWords.add(word);
                    // 2.(2) 目前word的数量(putWords中)比需要的数量(neededWords)多
                    if (putWords.count(word) > neededWords.count(word)) {
                        // 将left前移直到遇到匹配的word, 丢弃这个过程中的所有word
                        while (!(putWord = s.substring(left, left + oneWord)).equals(word)) {
                            putWords.remove(putWord, 1);
                            count--;
                            left += oneWord;
                        }
                        // 丢弃该匹配的word
                        left += oneWord;
                        count--;
                    }
                }
                // 2.(3) count足够: 找到一个所有匹配
                if (count == neededWordsNum) res.add(left);
            }
        }
        return res;
    }
}
