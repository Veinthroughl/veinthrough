package veinthrough.test.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.collection.CollectionToMap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static veinthrough.api.collection.CollectionToMap.RETAIN_MANNER.RETAIN_LAST;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Test TreeSet/TreeMap.
 *
 * 1. Comparable<T>/Comparator<T>:
 * Comparator<T> is more flexible than Comparable<T>
 * 2. Traverse TreeMap: {@link TraverseMapTest#mapTest()}
 * 3. Convert TreeSet to TreeMap: {@link CollectionToMapTest}
 * NOTE: CollectionToMap.toUniqueMap()中使用Stream/Collectors可能会改变元素顺序
 * 4. Navigation APIs methodReferenceTest of TreeMap.
 * 5. Navigation APIs methodReferenceTest of TreeSet.
 */
@Slf4j
public class TreeMapTest {
    @Test
    public void treeMapTraverseTest() {
        List<String> list = Lists.newArrayList(
                "Amilly", "Bob", "Carl", "Dougerance", "Erica", "Frances", "Gi");
        // use Comparable/natural Comparator
        Set<String> setNatural = new TreeSet<>(list);
        // use Comparator of name string, more flexible
        Set<String> setByLength = new TreeSet<>(Comparator.comparing(String::length));
        setByLength.addAll(setNatural);

        List<Map<Character, String>> maps = ImmutableList.of(setNatural, setByLength)
                .stream()
                // CollectionToMap.toUniqueMap()中使用Stream/Collectors可能会改变元素顺序
                .map(names -> CollectionToMap.toUniqueMap(
                        names,
                        str -> str.charAt(0),
                        RETAIN_LAST))
                .collect(Collectors.toList());
        List<Map<Character, String>> maps2 = ImmutableList.of(setNatural, setByLength)
                .stream()
                // use Maps.uniqueIndex()不会改变元素顺序
                .map(names -> Maps.uniqueIndex(names, str -> Objects.requireNonNull(str).charAt(0)))
                .collect(Collectors.toList());

        ImmutableMap.<String, Function<Map<Character, String>, String>>of(
                "Traverse by entry setNatural", TraverseMapTest::traverseByEntrySet,
                "Traverse by stream", TraverseMapTest::traverseByStream,
                "Traverse by key setNatural", TraverseMapTest::traverseByKeySet,
                "Traverse by values", TraverseMapTest::traverseByValues)
                .forEach((taskName, task) -> {
                    try {
                        // 顺序不一样
                        maps.forEach(names -> log.info(methodLog(
                                taskName + "/CollectionToMap.toUniqueMap()", task.apply(names))));
                        maps2.forEach(names -> log.info(methodLog(
                                taskName + "/Maps.uniqueIndex()", task.apply(names))));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, taskName));
                    }
                });
    }

    @Test
    public void treeMapApisTest() {
        // noinspection ConstantConditions
        TreeMap<Character, String> map =
                new TreeMap<>(Maps.uniqueIndex(
                        new TreeSet<>(Lists.newArrayList(
                                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria")),
                        str -> str.charAt(0)));
        log.info(methodLog(
                "descending map", "" + map.descendingMap(),
                "head map", "" + map.headMap('C'),
                "head map inclusive", "" + map.headMap('C', true),
                "tail map", "" + map.tailMap('C'),
                "tail map inclusive", "" + map.tailMap('C', true),
                "sub map", "" + map.subMap('C', 'F'),
                "sub map inclusive", "" + map.subMap('C', true, 'F', true),
                "first", map.firstKey() + ":" + map.firstEntry(),
                "last", map.lastKey() + ":" + map.lastEntry(),
                "floor", map.floorKey('C') + ":" + map.floorEntry('C'),
                "lower", map.lowerKey('C') + ":" + map.lowerEntry('C'),
                "ceiling", map.ceilingKey('C') + ":" + map.ceilingEntry('C'),
                "higher", map.higherKey('C') + ":" + map.higherEntry('C')));
    }

    @Test
    public void treeSetApisTest() {
        TreeSet<String> set = new TreeSet<>(Lists.newArrayList(
                "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria"));
        log.info(methodLog(
                "descending", "" + set.descendingSet(),
                "floor", "" + set.floor("Doug"),
                "lower", "" + set.lower("Doug"),
                "ceiling", "" + set.ceiling("Doug"),
                "higher", "" + set.higher("Doug"),
                "sub[)", "" + set.subSet("Bob", true, "Frances", false),
                "head exclusive", "" + set.headSet("Doug", false),
                "tail inclusive", "" + set.tailSet("Doug", true),
                // pollFirst()/first(): null/NoSuchElementException
                "first", "" + set.first(),
                // pollLast()/last(): null/NoSuchElementException
                "last", "" + set.last()
        ));
    }
}
