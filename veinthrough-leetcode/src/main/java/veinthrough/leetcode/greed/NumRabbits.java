package veinthrough.leetcode.greed;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第781题：
 * 森林中，每个兔子都有颜色。其中一些兔子【可能是全部】告诉你还有多少其他的兔子和自己有相同的颜色。
 * 我们将这些回答放在answers数组里。返回森林中兔子的最少数量。
 */
@SuppressWarnings("unused")
@Slf4j
public class NumRabbits {
    @Test
    public void test781() {
        Stream.of(new int[]{1, 1, 2},
                new int[]{10, 10, 10},
                new int[]{})
                .forEach(answers -> log.info(methodLog(
                        Arrays.toString(answers),
                        "" + numRabbits(answers))));
    }

    /**
     * 1. 使用贪心
     * 两只相同颜色的兔子看到的其他同色兔子数必然是相同的。反之，若两只兔子看到的其他同色兔子数不同，那么这两只兔子颜色也不同。
     * 因此，将answers中值相同的元素分为一组，对于每一组，计算出兔子的最少数量，然后将所有组的计算结果累加，就是最终的答案。
     * 例如，现在有13只兔子回答5。假设其中有一只红色的兔子，那么森林中必然有 6只红兔子。
     * 再假设其中还有一只蓝色的兔子，同样的道理森林中必然有6只蓝兔子。为了最小化可能的兔子数量，
     * 我们假设这12只兔子都在这 13只兔子中。那么还有一只额外的兔子回答5，这只兔子只能是其他的颜色，
     * 这一颜色的兔子也有6只。因此这种情况下最少会有18只兔子。
     *
     * 一般地，如果有x只兔子都回答y，则至少有⌈x/(y+1)⌉种不同的颜色，且每种颜色有 y+1只兔子，因此兔子数至少为
     * ⌈x/(y+1)⌉*(y+1)我们可以用哈希表统计answers中各个元素的出现次数，对每个元素套用上述公式计算，并将计算结果累加，即为最终答案。
     * 2. 将answers中值相同的元素分为一组:
     * (1) 使用map
     * (2) 使用guava中的Multiset
     */
    private static int numRabbits(int[] _answers) {
        // boundary
        if (_answers.length == 0) return 0;
        if (_answers.length == 1) return _answers[0] + 1;

        // 直接使用map
        return _numRabbits(_answers);
        // by multiset
//        return _numRabbitsByMultiset(_answers);

    }

    /**
     * 使用map.merge()按相同数字分组
     */
    private static int _numRabbits(int[] _answers) {

        // 1. 使用map.merge()按相同数字分组
        Map<Integer, Integer> answers = new HashMap<>();
        for (int answer : _answers)
            answers.merge(answer, 1, Integer::sum);

        // 2.
        int count;
        int sum = 0;
        for (Integer answer : answers.keySet()) {
            count = answers.get(answer);
            sum += Math.ceil((float) count / (answer + 1)) * (answer + 1);
        }
        return sum;
    }

    /**
     * 使用Multiset按相同数字分组
     */
    private static int _numRabbitsByMultiset(int[] _answers) {
        // 1. 使用Multiset按相同数字分组
        Multiset<Integer> answers = HashMultiset.create(
                Arrays.stream(_answers).boxed().collect(Collectors.toList()));

        // 2.
        int count;
        int sum = 0;
        for (Integer answer : answers.elementSet()) {
            count = answers.count(answer);
            sum += Math.ceil((float) count / (answer + 1)) * (answer + 1);
        }
        return sum;
    }
}
