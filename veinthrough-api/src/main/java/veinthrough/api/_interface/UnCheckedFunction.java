package veinthrough.api._interface;

import veinthrough.api.generic.Either;
import veinthrough.api.generic.Tuple;

import java.util.function.Function;

/**
 * 包装(出现RuntimeException)函数接口(因为可能出现异常).
 *
 * 将RuntimeException包装放入Either中,
 * (1) 未抛出异常, 返回值放入Either.right;
 * (2) 抛出异常:
 * > {@link #lift(UnCheckedFunction)}: exception放入Either.left;
 * > {@link #liftWithInput(UnCheckedFunction)}: input/exception用Pair包装放入Either.left.
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface UnCheckedFunction<T, R> {
    R apply(T t);

    /**
     * 2.(1) Either.left只包含exception
     */
    static <T, R> Function<T, Either> lift(UnCheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception ex) {
                return Either.left(ex);
            }
        };
    }

    /**
     * 2.(2) Either.left包含出错的input和exception, 使用Pair包装
     */
    static <T, R> Function<T, Either> liftWithInput(UnCheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception ex) {
                return Either.left(Tuple.of(ex, t));
            }
        };
    }
}
