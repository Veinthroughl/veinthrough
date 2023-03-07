package veinthrough.test.guava.collection;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.guava.SourceAndSinkTest;

import java.util.Collection;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * APIs:
 * 1. create:
 * [static] HashMultiset.create()
 * 2. add:
 * add(item)/ add(item, occurrences)/ addAll(Collection)
 * 3. contains:
 * contains(item)/ containsAll(collection)
 * [static] Multisets.containsOccurrences(multiset1, multiset2)
 * 4. remove:
 * remove(item)/ remove(item, occurrences)/ removeAll(collection)
 * 5. elementSet()/ entrySet()
 * 6. sort by occurrences:
 * [static] Multisets.copyHighestCountFirst(multiset)
 *
 * Tests:
 * 1. count:
 * {@link #wordCountTest()}/{@link #wordCountAndSortTest()}
 * {@link SourceAndSinkTest#readTest()}
 * 2. sort by occurrences function
 * {@link #wordCountAndSortTest()}
 * 3. contains methodReferenceTest:
 * (1) {@link Multiset#containsAll(Collection)}, 只考虑元素种类, 不考虑每个元素的count
 * (2) {@link Multisets#containsOccurrences(Multiset, Multiset)}, 考虑每个元素的count
 * 4. remove methodReferenceTest
 * (1) {@link Multiset#remove(Object)}/{@link Multiset#remove(Object, int count)}, 移出特定个数
 * (2) {@link Multiset#removeAll(Collection)}, 不考虑每个元素的count, 移除每个元素的所有occurrences
 */
@Slf4j
public class MultisetTest {
    private static final String words = "dr|wer|dfd|dd|dfd|dda|dfd|de|dr";
    private static final String word = "abc";
    private static final Multiset<String> multiset1 = HashMultiset.create();
    private static final Multiset<String> multiset2 = HashMultiset.create();

    static {
        multiset1.add(word, 2);
        multiset2.add(word, 6);
    }

    @Test
    public void wordCountTest() {
        Multiset<String> wordsMultiset = HashMultiset.create();
        wordsMultiset.addAll(
                Lists.newArrayList(words.split("\\|")));

        wordsMultiset.elementSet()
                .forEach(key -> log.info(methodLog(
                        key + " count: " + wordsMultiset.count(key))));
    }

    @Test
    public void wordCountAndSortTest() {
        Multiset<String> wordsMultiset = HashMultiset.create();
        wordsMultiset.addAll(
                Lists.newArrayList(words.split("\\|")));
        // 使用highestCountFirst排列元素
        //noinspection UnstableApiUsage
        Multisets.copyHighestCountFirst(wordsMultiset)
                .elementSet()
                .forEach(key -> log.info(methodLog(
                        key + " count: " + wordsMultiset.count(key))));
    }

    /**
     * contains test:
     * (1) {@link Multiset#containsAll(Collection)}, 只考虑元素种类, 不考虑每个元素的count
     * (2) {@link Multisets#containsOccurrences(Multiset, Multiset)}, 考虑每个元素的count
     */
    @Test
    public void containsTest() {
        // multiset1: [abc x 2],
        // multiset2: [abc x 6]
        log.info(methodLog(
                "multiset1", multiset1.toString(),
                "multiset2", multiset2.toString()));

        // return true, 因为包含了所有不重复元素
        log.info(methodLog(
                "multiset1.containsAll(multiset2): " +
                        multiset1.containsAll(multiset2)));

        // false: containsOccurrences(multiset1, multiset2)
        // true: containsOccurrences(multiset2, multiset1)
        log.info(methodLog(
                "Multisets.containsOccurrences(multiset1, multiset2): ",
                "" + Multisets.containsOccurrences(multiset1, multiset2),
                "Multisets.containsOccurrences(multiset2, multiset1): ",
                "" + Multisets.containsOccurrences(multiset2, multiset1)));
    }

    /**
     * 4. remove methodReferenceTest
     * (1) {@link Multiset#remove(Object)}/{@link Multiset#remove(Object, int count)}, 移出特定个数
     * (2) {@link Multiset#removeAll(Collection)}, 不考虑每个元素的count, 移除每个元素的所有occurrences
     */
    @Test
    public void removeTest() {
        // multiset1: [abc x 2],
        // multiset2: [abc x 6]
        log.info(methodLog(1,
                "multiset1", multiset1.toString(),
                "multiset2", multiset2.toString()));

        // multiset2 现在包含3个"abc"
        multiset2.remove(word, 3);
        log.info(methodLog(2,
                "After multiset2.remove(word, 3)",
                "multiset2", multiset2.toString()));

        // multiset2移除所有"abc", 虽然multiset1只有2个"a"
        multiset2.removeAll(multiset1);
        log.info(methodLog(3,
                "After multiset2.removeAll(multiset1)",
                "multiset2", multiset2.toString(),
                "multiset2.isEmpty(): ", "" + multiset2.isEmpty()));
    }
}
