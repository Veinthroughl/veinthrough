package veinthrough.test._enum;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author veinthrough
 *
 * A SIZE implementation by {@link Pair}.
 *
 * 1. enum解析:
 * (1) The enum declaration defines a class (called an enum type).
 * The enum class body can include methods and other fields.
 * The compiler automatically adds some special methods when it creates an enum.
 * For example, they have a static values method that returns an array containing all of the values of the enum in the order they are declared.
 * 首先，enum声明实际上定义了一个类。因此我们可以通过定义的enum调用其方法。
 * 其次，Java编译器会自动在enum类型中插入一些方法，其中就包括values()
 * ——所以我们的程序在没编译的时候，自然没法查看values()方法的源码了。
 * (2) All enums implicitly extend java.lang.Enum. Because a class can only extend one parent (see Declaring Classes),
 * the Java language does not support multiple inheritance of state (see Multiple Inheritance of State, Implementation, and Type),
 * and therefore an enum cannot extend anything else.
 * 这个枚举实际上是由java.lang.Enum这个类实现的，在程序中定义的枚举类型，都会隐式继承此类。
 * 并且，由于java中的继承是单继承，所以我们定义的枚举就无法在继承其他类了。
 * 2. 总结:
 * (1) java中的enum关键字背后实际是{@link Enum}这个类实现的。
 * (2) 在我们编写自定义的enum时，其中是不含values方法的，再编译java文件时，java编译器会自动帮助我们生成这个方法。
 *
 * # final class SIZE extends java.lang.Enum<SIZE> {
 * #   public static final SIZE INVALID;              // (1) 每个num实际上就是本类的一个static final对象
 * #   public static final SIZE TOO_SMALL;
 * #   ...
 * #   public static SIZE[] values();                 // (2) 编译器插入的方法
 * #   public static SIZE valueOf(java.lang.String);  // (2) 编译器插入的方法
 * #   public static SIZE forValue(Integer value);    // (3) 自己实现的方法
 * #   static {};
 * #   ...
 * # }
 */
public enum SIZE {
    TOO_SMALL(0, 1),  // (1) 除了这些值, 其他和类差不多
    SMALL(1, 10),
    MEDIUM(10, 20),
    LARGE(20, 50),
    EXTRA_LARGE(50, 100),
    TOO_LARGE(100, Integer.MAX_VALUE);

    @Getter
    private Pair<Integer, Integer> scope;
    private static final Map<Integer, SIZE> VALUE_MAP;

    static {
        final ImmutableMap.Builder<Integer, SIZE> mapByLeft = ImmutableMap.builder();
        for (SIZE enumItem : SIZE.values())
            mapByLeft.put(enumItem.getScope().getLeft(), enumItem);
        VALUE_MAP = mapByLeft.build();
    }

    SIZE(Integer minimum, Integer maximum) {
        this.scope = ImmutablePair.of(minimum, maximum);
    }

    public Pair<Integer, Integer> getValue() {
        return this.getScope();
    }

    public String getScopeString() {
        return "[" + this.scope.getLeft() + "," +
                this.scope.getRight() +
                ")";
    }

    @Override
    public String toString() {
        return this.name().toLowerCase() + getScopeString();
    }

    public static SIZE forValue(Integer value) throws InvalidSizeException {
        return VALUE_MAP.get(
                VALUE_MAP.keySet().stream()
                        .filter(left -> left <= value)
                        .max(Integer::compare)
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
