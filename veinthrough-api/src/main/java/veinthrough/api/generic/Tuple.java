package veinthrough.api.generic;

import lombok.*;

/**
 * @author veinthrough
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tuple<K,V> {
    @Getter @Setter
    private K first;
    @Getter @Setter
    private V second;

    @Override
    public String toString() {
        return "<" + getFirst().toString() + ","
                + getSecond().toString() + ">";
    }

    public static <K,V> Tuple<K, V> of(K key, V value) {
        return Tuple.<K,V>builder().first(key).second(value).build();
    }
}
