package veinthrough.test.collection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.collection.CollectionToMap;

import java.util.Map;
import java.util.function.Function;

import static veinthrough.api.collection.CollectionToMap.RETAIN_MANNER.RETAIN_LAST;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * This program demonstrate different ways of traversing Map.
 *
 * 1. Traverse by entry set: Map.entrySet()
 * 2. Traverse by stream: Map.forEach()
 * 3. Traverse by key set: Map.keySet()
 * 4. Traverse by values: map.values()
 */
@Slf4j
public class TraverseMapTest {
    @Test
    public void mapTest() {
        Map<Character, String> map = CollectionToMap.toUniqueMap(
                Lists.newArrayList(
                        "Amy", "Bob", "Carl", "Doug", "Erica", "Frances", "Gloria"),
                str -> str.charAt(0),
                RETAIN_LAST);
        ImmutableMap.<String, Function<Map<Character, String>, String>>of(
                "Traverse by entry set", TraverseMapTest::traverseByEntrySet,
                "Traverse by stream", TraverseMapTest::traverseByStream,
                "Traverse by key set", TraverseMapTest::traverseByKeySet,
                "Traverse by values", TraverseMapTest::traverseByValues)
                .forEach((taskName, task) -> {
                    try {
                        log.info(methodLog(taskName, task.apply(map)));
                    } catch (Exception e) {
                        log.error(exceptionLog(e, taskName));
                    }
                });
    }

    /**
     * Traverse by entry set: Map.entrySet()
     */
    static <K, V> String traverseByEntrySet(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        // entry
        for (Map.Entry<K, V> entry : map.entrySet()) {
            handleKeyValue(result, entry.getKey(), entry.getValue());
        }
        return stringWithBrace(result);
    }

    /**
     * Traverse by stream: Map.forEach()
     */
    static <K, V> String traverseByStream(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        // (key, value)
        map.forEach((key, value) ->
                handleKeyValue(result, key, value));
        return stringWithBrace(result);
    }

    /**
     * Traverse by key set: Map.keySet()
     */
    static <K, V> String traverseByKeySet(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        // key
        map.keySet().forEach(key ->
                handleKeyValue(result, key, map.get(key)));
        return stringWithBrace(result);
    }

    /**
     * Traverse by values: map.values()
     */
    static <K, V> String traverseByValues(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        map.values().forEach(value ->
                handleKeyValue(result, value));
        return stringWithBrace(result);
    }

    private static <K, V> void handleKeyValue(StringBuilder stringBuilder, K key, V value) {
        stringBuilder.append(key)
                .append(":")
                .append(value)
                .append(", ");
    }

    private static <V> void handleKeyValue(StringBuilder stringBuilder, V value) {
        stringBuilder.append(value)
                .append(", ");
    }

    private static String stringWithBrace(StringBuilder stringBuilder) {
        int length = stringBuilder.length();
        return stringBuilder.delete(length - 2, length)
                .insert(0, "{")
                .append("}")
                .toString();
    }
}
