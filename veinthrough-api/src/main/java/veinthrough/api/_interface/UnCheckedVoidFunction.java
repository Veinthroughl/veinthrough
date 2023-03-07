package veinthrough.api._interface;

import veinthrough.api.generic.Either;
import veinthrough.api.generic.Tuple;

import java.util.function.Function;

/**
 * 包装(出现RuntimeException)函数接口(因为可能出现异常).
 *
 * 将RuntimeException包装放入Either中,
 * (1) 未抛出异常, null放入Either.right;
 * (2) 抛出异常:
 * > {@link #lift(UnCheckedVoidFunction)}: exception放入Either.left;
 * > {@link #liftWithInput(UnCheckedVoidFunction)}: input/exception用Pair包装放入Either.left.
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface UnCheckedVoidFunction<T> {
    void apply(T t);

    /**
     * 2.(1) Either.left只包含exception
     */
    static <T> Function<T, Either> lift(UnCheckedVoidFunction<T> function) {
        return t -> {
            try {
                function.apply(t);
                return Either.right(null);
            } catch (Exception ex) {
                return Either.left(ex);
            }
        };
    }

    /**
     * 2.(2) Either.left包含出错的input和exception, 使用Pair包装
     */
    static <T> Function<T, Either> liftWithInput(UnCheckedVoidFunction<T> function) {
        return t -> {
            try {
                function.apply(t);
                return Either.right(null);
            } catch (Exception ex) {
                return Either.left(Tuple.of(ex, t));
            }
        };
    }
}
