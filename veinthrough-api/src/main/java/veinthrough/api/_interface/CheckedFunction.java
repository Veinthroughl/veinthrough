package veinthrough.api._interface;

import veinthrough.api.generic.Either;
import veinthrough.api.generic.Tuple;

import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * 包装函数接口(因为可能出现异常).
 *
 * 1. 遇到CheckedException转化成RuntimeException直接终止,
 * 因为多线程lambda中出现异常只是执行该lambda的线程悄悄结束
 * 2. 将CheckedException包装放入Either中,
 * (1) 未抛出异常, 返回值放入Either.right;
 * (2) 抛出异常:
 * > {@link #lift(CheckedFunction)}: exception放入Either.left;
 * > {@link #liftWithInput(CheckedFunction)}: input/exception用Pair包装放入Either.left.
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface CheckedFunction<T, R> {
    R apply(T t) throws Exception;

    /**
     * 1.
     */
    static <T, R> Function<T, R> wrap(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 1.
     */
    static <T> ToIntFunction<T> wrapInt(CheckedFunction<T,Integer> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 2.(1) Either.left只包含exception
     */
    static <T, R> Function<T, Either> lift(CheckedFunction<T, R> function) {
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
    static <T, R> Function<T, Either> liftWithInput(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception ex) {
                return Either.left(Tuple.of(ex, t));
            }
        };
    }
}
