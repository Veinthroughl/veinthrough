package veinthrough.api.generic;

import lombok.*;

/**
 * @author veinthrough
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pair<T> {
    @Getter
    @Setter
    private T first;
    @Getter
    @Setter
    private T second;

    @SuppressWarnings("unused")
    public void reverse() {
        T t = first;
        first = second;
        second = t;
    }

    @Override
    public String toString() {
        return "<" + getFirst().toString() + ","
                + getSecond().toString() + ">";
    }

    public static <T> Pair<T> of(T first, T second) {
        return Pair.<T>builder().first(first).second(second).build();
    }
}
