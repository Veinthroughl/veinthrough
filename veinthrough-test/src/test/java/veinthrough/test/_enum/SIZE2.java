package veinthrough.test._enum;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author veinthrough
 *
 * A SIZE implementation by {@link Range}.
 * enum的详细说明见{@link SIZE}.
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public enum SIZE2 {
    TOO_SMALL(0, 1),
    SMALL(1, 10),
    MEDIUM(10, 20),
    LARGE(20, 50),
    EXTRA_LARGE(50, 100),
    TOO_LARGE(100, Integer.MAX_VALUE);

//    private static final Range<Integer> INVALID_RANGE = Range.singleton(-1);

    @Getter
    final private Range<Integer> scope;         // 作为key, 应该为final, 但这里因Range可修改, 作为key不太明智
    private static final Map<Range<Integer>, SIZE2> VALUE_MAP;

    static {
        final ImmutableMap.Builder<Range<Integer>, SIZE2> sizes = ImmutableMap.builder();
        for (SIZE2 enumItem : SIZE2.values()) {
            sizes.put(enumItem.getScope(), enumItem);
        }
        VALUE_MAP = sizes.build();
    }

    SIZE2(Integer min, Integer max) {
        this.scope = Range.closedOpen(min, max);
    }

    public String getScopeString() {
        return this.scope.toString();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase() + getScopeString();
    }

    public static SIZE2 forValue(Integer value) throws InvalidSizeException {
        return VALUE_MAP.get(
                VALUE_MAP.keySet().stream()
                        .filter(scope ->
                                scope.contains(value))
                        .findFirst()
                        .orElseThrow(() ->
                                new InvalidSizeException("size should between(0, Integer.MAX_VALUE)")));
    }

    @NoArgsConstructor
    public static class InvalidSizeException extends Exception {
        private InvalidSizeException(String message) {
            super(message);
        }
    }
}
