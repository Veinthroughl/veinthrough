package veinthrough.test.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.Arrays;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Tests:
 * 1. Convert array to string: Arrays.toString(array), can't use array.toString()
 * 2. {@link System#arraycopy(Object, int, Object, int, int)},
 * 参数用的是Object才能同时兼容基本类型的数组/非基本类型的数组
 * (1) 可以接受基本类型的数组: 比如int[]
 * (2) 可以接受非基本类型的数组: T[]
 * 3. Arrays.copyOf()/copyOfRange(),
 * Arrays.copyOf需要实现多个函数才能兼容基本类型的数组/非基本类型的数组:
 * {@link Arrays#copyOf(int[], int)}/{@link Arrays#equals(long[], long[])}
 * {@link Arrays#copyOf(Object[], int, Class)}, 这里Object为泛型T
 * 4. Object[]作为参数: {@link Array#badCopyOf(Object[], int)}
 * (1) 不能接受基本类型的数组: 比如int[]
 * (2) 返回的Object[]不能转换
 * 5. Object作为参数: {@link Array#copyOf(Object, int)},
 * 类似{@link System#arraycopy(Object, int, Object, int, int)}中也是Object作为参数
 */
@Slf4j
public class ArrayCopyTest {
    private static final int[] intArray = {1, 2, 3};
    private static final String[] stringArray = {"Tom", "Dick", "Harry"};

    /**
     * 参数用的是Object才能同时兼容基本类型的数组/非基本类型的数组
     * (1) 可以接受基本类型的数组: 比如int[]
     * (2) 可以接受非基本类型的数组: T[]
     * {@link System#arraycopy(Object, int, Object, int, int)}
     */
    @Test
    public void systemArrayCopyTest() {
        String[] copy = new String[stringArray.length];
        System.arraycopy(stringArray, 0, copy, 0, stringArray.length);
        log.info(methodLog("copy", Arrays.toString(copy)));
    }

    /**
     * Arrays.copyOf需要实现多个函数才能兼容基本类型的数组/非基本类型的数组:
     * {@link Arrays#copyOf(int[], int)}/{@link Arrays#equals(long[], long[])}
     * {@link Arrays#copyOf(Object[], int, Class)}, 这里Object为泛型T
     */
    @Test
    public void arraysArrayCopyTest() {
        String[] copy = Arrays.copyOf(stringArray, stringArray.length);
        log.info(methodLog("copy", Arrays.toString(copy)));
    }

    /**
     * Object[]作为参数: {@link Array#badCopyOf(Object[], int)}
     * (1) 不能接受基本类型的数组: 比如int[]
     * (2) 返回的Object[]不能转换成String[]
     */
    @Test
    @SuppressWarnings("deprecation")
    public void badArrayCopyOfTest() {
        // int[] argument can't be converted to object[] parameter
        // can't accept int[] as argument
//        String[] copy = (String[]) Array.badCopyOf(intArray, 10);

        log.info(methodLog("The following call will generate an exception."));
        // Object[] can't be converted to String[]
        // 从定义开始的Object[]不能转化成其他数组
        // 除非是object[]作为中间状态, 从其他数组转换成object[]再转换成其他数组
        // 如String[] -> Object[] -> String[], 因为Java数组会记住每个元素的类型
        // java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String
        String[] copy = (String[]) Array.badCopyOf(stringArray, 10);
        log.info(methodLog("copy", Arrays.toString(copy)));
    }

    /**
     * Object作为参数: {@link Array#copyOf(Object, int)},
     * {@link System#arraycopy(Object, int, Object, int, int)}中也是Object作为参数
     */
    @Test
    public void goodArrayCopyOfTest() {
        // Object can be converted to int[]
        int[] copy_int = (int[]) Array.copyOf(intArray, 10);
        log.info(methodLog("copy", Arrays.toString(copy_int)));

        // Object can be converted to String[]
        String[] copy_string = (String[]) Array.copyOf(stringArray, 10);
        log.info(methodLog("copy", Arrays.toString(copy_string)));
    }
}
