package veinthrough.test.collection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api._interface.Identifiable;
import veinthrough.api.collection.CollectionToMap;
import veinthrough.test._class.Employee;
import veinthrough.test._class.Manager;
import veinthrough.test.guava.collection.MultiMapTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static veinthrough.api.collection.CollectionToMap.RETAIN_MANNER.RETAIN_FIRST;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. 使用{@link Collectors#toMap}: {@link #byToMapTest()}
 * 2. 使用{@link Collectors#groupingBy(Function)}: {@link #byGroupingByTest()},
 * 对duplicate key的方式只能是list
 * 3. 使用{@link CollectionToMap}: {@link #integrateTest()}
 * 4. 使用{@link Multimaps#index}, 效果同{@link CollectionToMap#toListedMap}:
 * {@link MultiMapTest#listToMapTest()}
 * 也就是说对duplicate key的方式只能是list
 * 5. 使用{@link Maps#uniqueIndex}:
 * {@link MapTest#createFromKeysTest()}, {@link MapTest#createFromValuesTest()}
 * {@link TreeMapTest#treeMapTraverseTest()}: CollectionToMap.toUniqueMap()中使用Stream/Collectors可能会改变元素顺序
 * 出现duplicate keys: IllegalArgumentException
 */
@Slf4j
public class CollectionToMapTest {
    private static final Employee worker = new Employee("src/main/java/veinthrough", 60000D);
    private static final Manager cfo =
            new Manager("Sid Sneaky", 800000D, 60000D);
    private static final Manager ceo =
            new Manager("Gus Greedy", 1000000D, 80000D);

    private List<String> getDataList() {
        return Lists.newArrayList("aardvark", "elephant", "koala", "eagle", "kangaroo");
    }

    /**
     * 使用{@link Collectors#toMap}:
     * {@link Collectors#toMap(Function keyMapper, Function valueMapper)},
     * {@link Collectors#toMap(Function, Function, BinaryOperator mergeFunction)}
     * {@link Collectors#toMap(Function, Function, BinaryOperator mergeFunction, Supplier mapSupplier)}
     * 1. Key:
     * (1) use lambda String.charAt(0) as key
     * (2) use Identifiable.getIdentifier() as key
     * 2. Value:
     * (1) item -> item
     * (2) Function.identity(), 实际上就是item -> item
     * 3. Duplicate key:
     * (1) error(java.lang.IllegalStateException: Duplicate key elephant)
     * (2) override(retain first/last)
     * (3) list, {@link Collectors#toMap}不能实现
     */
    @Test
    public void byToMapTest() {
        // duplicate key: error(java.lang.IllegalStateException: Duplicate key elephant)
        log.info(methodLog("" +
                getDataList().stream()
                        .collect(Collectors.toMap(
                                str -> str.charAt(0),
                                str -> str))));
        // duplicate key: error(java.lang.IllegalStateException: Duplicate key elephant)
        log.info(methodLog("" +
                getDataList().stream()
                        .collect(Collectors.toMap(
                                str -> str.charAt(0),
                                Function.identity()))));   // identity()实际上就是t -> t
        // duplicate key: override(retain first/last)
        log.info(methodLog("" +
                getDataList().stream()
                        .collect(Collectors.toMap(
                                str -> str.charAt(0),
                                Function.identity(),
                                (key1, key2) -> key2))));
        // duplicate key: override(retain first/last)
        // LinkedHashMap::new is the implementation of the map
        log.info(methodLog("" +
                getDataList().stream()
                        .collect(Collectors.toMap(
                                str -> str.charAt(0),
                                Function.identity(),
                                (key1, key2) -> key2,
                                LinkedHashMap::new))));
    }

    /**
     * 使用@link Collectors#groupingBy(Function)}:
     * duplicate key: list
     */
    @Test
    public void byGroupingByTest() {
        log.info(methodLog("" +
                getDataList().stream()
                        .collect(Collectors.groupingBy(str -> str.charAt(0)))));
    }

    /**
     * 使用{@link CollectionToMap}
     * 实际上CollectionToMap是综合{@link Collectors#toMap}/{@link Collectors#groupingBy(Function)},
     * 同时还考虑了Collection元素为{@link Identifiable}时，keyFunction直接为{@link Identifiable#getIdentifier()},
     * 另外注意实现中使用Stream/Collectors可能会改变元素顺序.
     * 1. non-identifiable + override(retain first/last)
     * 2. identifiable + override(retain first/last)
     * 3. non-identifiable + list, 效果同{@link Multimaps#index}
     * 4. identifiable + list, 效果同{@link Multimaps#index}
     */
    @Test
    public void integrateTest() {
        // non-identifiable + override(retain first/last)
        log.info(methodLog(1,
                CollectionToMap.toUniqueMap(
                        getDataList(),
                        str -> str.charAt(0),
                        RETAIN_FIRST)
                        .toString()));
        // identifiable + override(retain first/last)
        log.info(methodLog(2,
                CollectionToMap.toUniqueMap(
                        Lists.newArrayList(worker, ceo, cfo),
                        RETAIN_FIRST)
                        .toString()));
        // non-identifiable + list
        log.info(methodLog(3,
                CollectionToMap.toListedMap(
                        getDataList(),
                        str -> str.charAt(0))
                        .toString()));
        // identifiable + list
        log.info(methodLog(4,
                CollectionToMap.toListedMap(
                        Lists.newArrayList(worker, ceo, cfo))
                        .toString()));
    }
}
