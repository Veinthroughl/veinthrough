package veinthrough.leetcode.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第49题：
 * 给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。
 * 字母异位词 是由重新排列源单词的字母得到的一个新单词，所有源单词中的字母通常恰好只用一次。
 *
 * 示例 1:
 * 输入: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出: [["bat"],["nat","tan"],["ate","eat","tea"]]
 */
@Slf4j
public class GroupAnagrams {
    @Test
    public void test49() {
        Stream.of(
                new String[]{"eat", "tea", "tan", "ate", "nat", "bat"},
                new String[]{""})
                .forEach(strs -> log.info(methodLog(
                        Arrays.toString(strs), groupAnagrams(strs).toString())));
    }

    /**
     * 方法1: key: 每个单词按字母排序, value: 对应的单词
     * 方法2: key: 对每个单词统计其包含字母a-z的个数, 然后从a-z按个数合组装起来
     * value: 对应的单词
     */
    private List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> res = new LinkedList<>();
        // boundary
        if (strs == null || strs.length == 0) return res;
        if (strs.length == 1) {
            res.add(Collections.singletonList(strs[0]));
            return res;
        }
        int len = strs.length;
        Map<String, List<String>> map =
                new HashMap<>(len >= 32 ? len / 2 : len);
        for (String str : strs) {
            // (1) sort
            String _str = str;
            char[] charArray = _str.toCharArray();
            Arrays.sort(charArray);
            _str = new String(charArray);
            // (2) put into map
            List<String> value = new LinkedList<>();
            value.add(str);
            map.merge(_str, value,
                    (original, newValue) -> {
                        original.addAll(newValue);
                        return original;
                    });
        }
        // (3) get list from map
        res.addAll(map.values());

        return res;
    }
}
