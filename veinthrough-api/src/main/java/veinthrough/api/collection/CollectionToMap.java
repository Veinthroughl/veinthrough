package veinthrough.api.collection;

import com.google.common.collect.Multimaps;
import veinthrough.api._interface.Identifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author veinthrough
 *
 * 综合{@link Collectors#toMap}/{@link Collectors#groupingBy(Function)}, 来实现 list --> map,
 * 同时还考虑了Collection元素为{@link Identifiable}时，keyFunction直接为{@link Identifiable#getIdentifier()},
 * 另外注意实现中使用Stream/Collectors可能会改变元素顺序.
 * 1. non-identifiable + override(retain first/last)
 * 2. identifiable + override(retain first/last)
 * 3. non-identifiable + list, 效果同{@link Multimaps#index}
 * 4. identifiable + list, 效果同{@link Multimaps#index}
 */
public class CollectionToMap {
    /**
     * 1. non-identifiable + override + retain first/last
     */
    public static <K, T> Map<K, T> toUniqueMap(Collection<T> collection,
                                               Function<? super T, ? extends K> keyFunction,
                                               RETAIN_MANNER manner) {
        return collection.stream()
                .collect(Collectors.toMap(
                        keyFunction,
                        Function.identity(),
                        retainFunction(manner)));
    }

    /**
     * 2. identifiable + override + retain first/last
     */
    public static <K, T extends Identifiable<K>> Map<K, T> toUniqueMap(Collection<T> collection,
                                                                       RETAIN_MANNER manner) {
        return toUniqueMap(collection, T::getIdentifier, manner);
    }


    /**
     * 3. non-identifiable + list, 效果同{@link Multimaps#index(Iterable, com.google.common.base.Function)},
     * The same effect:
     * (1) toListedMap(list, keyFunction)
     * (2) MultiMaps.index(list, keyFunction)
     */
    public static <K, T> Map<K, List<T>> toListedMap(Collection<T> collection,
                                                     Function<? super T, ? extends K> keyFunction) {
        return collection.stream().collect(Collectors.groupingBy(keyFunction));
    }


    /**
     * 4. identifiable + list, 效果同{@link Multimaps#index(Iterable, com.google.common.base.Function)}.
     */
    public static <K, T extends Identifiable<K>> Map<K, List<T>> toListedMap(Collection<T> collection) {
        return toListedMap(collection, T::getIdentifier);
    }

    private static <K> BinaryOperator<K> retainFunction(RETAIN_MANNER manner) {
        return manner == RETAIN_MANNER.RETAIN_FIRST ?
                (k1, k2) -> k1 : (k1, k2) -> k2;
    }

    public enum RETAIN_MANNER {
        RETAIN_FIRST,
        RETAIN_LAST
    }
}
