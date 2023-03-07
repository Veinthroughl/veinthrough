package veinthrough.api.generic;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Comparator;

public class PairAlg {
    // <T extends Comparable<? super T>
    public static <T extends Comparable<? super T>> Pair<T> minMax(T[] a) {
        Preconditions.checkArgument(a != null && a.length > 0);
        return minMax(Arrays.asList(a));
    }


    // <T extends Comparable<? super T>
    public static <T extends Comparable<? super T>> Pair<T> minMax(Iterable<T> a) {
        Preconditions.checkArgument(a != null && a.iterator().hasNext());

        // naturalOrder等价于: (left, right) -> left.compareTo(right)
        Ordering<T> ordering = Ordering.<T>from(
                Comparator.naturalOrder());
        return Pair.<T>builder()
                .first(ordering.min(a))
                .second(ordering.max(a))
                .build();
    }
}