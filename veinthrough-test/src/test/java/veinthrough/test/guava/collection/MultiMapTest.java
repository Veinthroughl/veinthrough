package veinthrough.test.guava.collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.collection.CollectionToMap;
import veinthrough.test.collection.CollectionToMapTest;

import java.util.Collection;
import java.util.Objects;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Tests:
 * 1. Convert a list to a map, the same effect:
 * (1) by multimap: {@link Multimaps#index(Iterable, Function)}
 * (2) by CollectionToMap, values  -->  map
 * {@link CollectionToMap#toListedMap(Collection, java.util.function.Function)}
 * (3) by Maps, keys  -->  map
 * {@link com.google.common.collect.Maps#toMap(Iterable, Function)}
 * {@link com.google.common.collect.Maps#uniqueIndex(Iterable, Function)}
 * 2. 其他Collection --> Map: {@link CollectionToMapTest}
 */
@Slf4j
public class MultiMapTest {
    private static final String[] animals =
            new String[]{"aardvark", "elephant", "koala", "eagle", "kangaroo"};

    /**
     * Convert a list to a map, the same effect:
     * (1) by multimap: Multimaps.index(list, keyFunction)
     * (2) by CollectionToMap: toListedMap(list, keyFunction)
     */
    @Test
    public void listToMapTest() {
        ImmutableList<String> animalsList = ImmutableList.copyOf(animals);

        // (1) by multimap
        // a=[aardvark], e=[elephant, eagle], k=[koala, kangaroo]
        ImmutableListMultimap<Character, String> animalsByFirstLetter =
                Multimaps.index(animalsList,
                        str -> Objects.requireNonNull(str).charAt(0));
        log.info(methodLog(animalsByFirstLetter.toString()));

        // (2) by CollectionToMap
        log.info(methodLog(
                CollectionToMap.toListedMap(
                        animalsList,
                        str -> str.charAt(0))
                        .toString()
        ));
    }
}
