package veinthrough.test.collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * TODO LinkedHashMap
 * TODO WeakHashMap
 * SortedMap/NavigableMap/TreeMap: {@link TreeMapTest#treeMapApisTest()}/{@link TreeMapTest#treeSetApisTest()}
 */
@Slf4j
public class MapTest {
    /**
     * 快速创建map:
     * (1) 使用双括号, 可以创建的时候直接初始化, 但是不能使用<>
     * (2) 使用Guava中的{@link ImmutableMap}, 得到的map不可修改
     */
    @Test
    public void createTest() {
        // 双括号: 这里不能使用<>而只能用<Integer,String>.
        Map<Integer, String> mapA = new HashMap<Integer, String>() {{
            put(1, "1");
            put(2, "2");
            put(3, "3");
        }};
        Map<Integer, String> mapB = ImmutableMap.<Integer, String>builder()
                .put(1, "1")
                .put(2, "2")
                .put(3, "3")
                .build();
        log.info(methodLog(
                "mapA", "" + mapA,
                "mapB", "" + mapB));
    }

    /**
     * 1. 通过{@link Map#keySet()}得到的Set只是一个视图;
     * 修改这个set会同时修改map
     */
    @Test
    public void setViewTest() {
        Map<Integer, String> mapA = new HashMap<Integer, String>() {{
            put(1, "1");
            put(2, "2");
            put(3, "3");
        }};
        // 1. set view
        Set<Integer> setA = mapA.keySet();              // view, 修改setA将影响mapA
        Set<Integer> setB = new HashSet<>(setA);        // setB和setA独立
        Set<Integer> setC = new HashSet<>();
        // noinspection CollectionAddAllCanBeReplacedWithConstructor
        setC.addAll(setA);                              // setC和setA独立
        log.info(methodLog(1,
                "mapA", "" + mapA,
                "setA", "" + setA,
                "setB", "" + setB,
                "setC", "" + setC));

        // 2. remove setA(view of map) will affect mapA
        setA.remove(1);
        setB.remove(2);
        setC.remove(3);
        log.info(methodLog(2, "After remove setB from setA",
                "mapA", "" + mapA,
                "setA", "" + setA,
                "setB", "" + setB,
                "setC", "" + setC));
    }

    /**
     * 也可以使用Multiset of Guava来实现这个功能
     */
    @Test
    public void mergeTest() {
        int[] nums = new int[]{9, 4, 9, 8, 4};
        Map<Integer, Integer> map = new HashMap<>(nums.length / 2);
        // 统计每个数字出现的次数, 不存在则初始化为1， 存在则+1
        for (int num : nums)
            map.merge(num, 1, Integer::sum);
        log.info(methodLog(map.toString()));
    }

    /**
     * {@link ConcurrentHashMap#ConcurrentHashMap()}
     * {@link ConcurrentHashMap#ConcurrentHashMap(int)}
     * {@link ConcurrentHashMap#ConcurrentHashMap(int, float)}
     * {@link ConcurrentHashMap#ConcurrentHashMap(int, float, int)}
     * {@link ConcurrentHashMap#ConcurrentHashMap(Map)}
     */
    @Test
    public void capacityTest()
            throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // initialCapacity:11  -->  capacity:16, threshold:12
        // initialCapacity:17  -->  capacity:32, threshold:24
        Map<Integer, Integer> map = new HashMap<>(11);
        Class<?> mapType = map.getClass();
        Field threshold = mapType.getDeclaredField("threshold");
        threshold.setAccessible(true);
        Method capacity = mapType.getDeclaredMethod("capacity");
        capacity.setAccessible(true);
        log.info(methodLog(
                "Before put----------------------------",
                "容量", "" + capacity.invoke(map),
                "阈值", "" + threshold.get(map),
                "元素数量", "" + map.size()));

        map.put(1, 2);
        log.info(methodLog(
                "After put-----------------------------",
                "容量", "" + capacity.invoke(map),
                "阈值", "" + threshold.get(map),
                "元素数量", "" + map.size()));
        for (int i = 0; i < 17; i++) {
            map.put(i, i);
            log.info(methodLog(
                    "容量", "" + capacity.invoke(map),
                    "阈值", "" + threshold.get(map),
                    "元素数量", "" + map.size()));
        }
    }

    @Test
    public void createFromKeysTest() {
        List<Integer> keysList = ImmutableList.of(1, 2, 3, 3);
        Set<Integer> keysSet = new HashSet<>(keysList);
        log.info(methodLog(
                "set as keys", "" + Maps.asMap(keysSet, String::valueOf),
                "iterable as keys", "" + Maps.toMap(keysList, String::valueOf)));
    }

    /**
     * 也可以使用{@link com.google.common.collect.Multimaps#index(Iterable, Function)}
     */
    @SuppressWarnings("unused")
    @Test
    public void createFromValuesTest() {
        List<String> valuesList = ImmutableList.of("1", "2", "3");
        List<String> duplicateValuesList = ImmutableList.of("1", "2", "3", "3");
        log.info(methodLog(
                // IllegalArgumentException, as duplicate key
//                "iterable as values", ""+ Maps.uniqueIndex(duplicateValuesList, Integer::valueOf),
                "iterable as values", "" + Maps.uniqueIndex(valuesList, Integer::valueOf)));
    }

    /**
     * {@link CollectionToMapTest}
     */
    @Test
    public void collectionToMapTest(){}
}
