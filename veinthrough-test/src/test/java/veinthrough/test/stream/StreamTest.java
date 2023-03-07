package veinthrough.test.stream;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test._class.Manager;
import veinthrough.test.async.WordCounter;
import veinthrough.test.collection.TraverseTest;
import veinthrough.test.string.CheckSumTest;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. list to map: {@link veinthrough.api.collection.CollectionToMap}
 * 2. Collection <--> Stream<T> <--> IntStream
 * 特殊的Stream会有额外的功能(比如IntStream的sum)
 * {@link WordCounter.ForkJoinCounter#compute()} , Stream<T> -> IntStream
 * {@link TraverseTest#arrayListTest()} , IntStream -> Stream<Integer> -> List<T>
 * {@link TraverseTest#vectorTest()}, IntStream -> Stream<Integer> -> Vector<T>
 * 3. range of integer
 * (1) {@link IntStream#range(int, int)}
 * (2) {@link Stream#iterate(Object initialValue, UnaryOperator)}
 * Java 9: Stream.iterate(initial value, Predicate, next value), supports a predicate (condition),
 * and the stream.iterate will stop if the predicate is false.
 * (3) Guava's Range haven't stream operations: {@link veinthrough.test.guava.RangeTest}
 * 4. array --> stream
 * (1) {@link Stream#of(Object[])}: 只能处理Object[], 会把int[]{1,2}整个当作Object处理
 * {@link Arrays#asList(Object[])}同样只能处理Object[], 会把int[]{1,2}整个当作Object处理
 * (2) 实际上Arrays.stream有多个重载函数: {@link Arrays#stream(int[])}/{@link Arrays#stream(Object[])}
 * 5. String --> char[]
 * (1) 方法1(不行): 使用{@link String#chars()}, 返回IntStream, 没有CharStream,
 * {@link veinthrough.test.string.CheckSumTest#checkSum(String)}
 * (2) 方法2: {@link String#toCharArray()}
 * 7. reduce:
 * (1) {@link Stream#reduce(BinaryOperator)},
 * Stream<T> --> BinaryOperator对T操作
 * (2) {@link Stream#reduce(Object initValue, BinaryOperator)},
 * Object: T类型初始值,
 * Stream<T> --> BinaryOperator对T操作
 * (3) {@link Stream#reduce(Object, BiFunction, BinaryOperator)}: 感觉是map+reduce(2)的功能
 * Object: U类型初始值, 和T不同
 * BiFunction: T+U --> U的map功能
 * BinaryOperator: 对U操作
 * 这里为什么同时需要BiFunction/BinaryOperator这两个参数, 为了串行/并行的统一
 * > 串行: 只需要BiFunction
 * > 并行: 每个线程内需要BiFunction, 线程间的合并需要BinaryOperator
 * {@link CheckSumTest#checkSumByLine()},
 * (4) {@link LongStream#reduce(long, LongBinaryOperator)},
 * {@link CheckSumTest#checkSum(String)}
 */
@Slf4j
public class StreamTest {
    /**
     * 3.(1) {@link IntStream#range(int startInclusive, int endExclusive)}
     * get stream of wrapper objects which can be collected by Collectors methods
     */
    @Test
    public void integerRangeTest() {
        log.info(methodLog(
                "Range Array",
                Arrays.toString(IntStream.range(1, 50).toArray()),
                "Range List",
                IntStream.range(1, 50)
                        // IntStream -> Stream<Integer>才能使用collect
                        .boxed()
                        .collect(Collectors.toList())
                        .toString()));
    }

    /**
     * 3.(1) {@link IntStream#range(int startInclusive, int endExclusive)}
     */
    @Test
    public void rangeStreamAsIndex() {
        final int numPerLine = 10;
        List<String> charsets =
                Lists.newArrayList(Charset.availableCharsets().keySet());
        IntStream.range(0, charsets.size())
                .forEach(index -> {
                    if (index == charsets.size() - 1) System.out.println(charsets.get(index));
                    else if (index > 0 && index % numPerLine == 0) System.out.println(charsets.get(index) + ",");
                    else System.out.print(charsets.get(index) + ",");
                });
    }

    /**
     * 3.(2) {@link Stream#iterate(Object, UnaryOperator)}
     */
    @Test
    public void rangeStreamByIterate() {
        log.info(methodLog(
                // 0 ... 9
                "10 numbers", Stream.iterate(0, n -> n + 1)
                        .limit(10)
                        .collect(Collectors.toList())
                        .toString(),
                // Java 9: Stream.iterate(initial value, Predicate, next value), supports a predicate (condition),
                // and the stream.iterate will stop if the predicate is false.
//                Stream.iterate(0, n -> n < 10 , n -> n + 1)

                // odds
                "10 odds", Stream.iterate(0, n -> n + 1)
                        .filter(x -> x % 2 != 0)
                        .limit(10)
                        .collect(Collectors.toList()).toString(),

                // Fibonacci
                "Fibonacci", Stream.iterate(new int[]{0, 1}, bi -> new int[]{bi[1], bi[0] + bi[1]})
                        .limit(10)
                        .map(n -> n[0])
                        .collect(Collectors.toList())
                        .toString()));
    }

    /**
     * array --> stream
     * (1) {@link Stream#of(Object[])}: 只能处理Object[], 会把int[]{1,2}整个当作Object处理
     * (2) {@link Arrays#stream(int[])}/{@link Arrays#stream(Object[])}
     */
    @Test
    public void arrayToStreamTest() {
        final Manager ceo =
                new Manager("Gus Greedy", 800000D, 80000D);
        final Manager cfo =
                new Manager("Sid Sneaky", 600000D, 60000D);
        final Manager[] managers = {ceo, cfo};
        log.info(methodLog(
                "Stream.of(new int[]{1, 2})", // --> Stream<Object>
                // Stream.of(T...)/Arrays.asList(T...)都要求是Object[]
                // 会把int[]{1,2}整个当作Object处理
                "" + Stream.of(new int[]{1, 2}).collect(Collectors.toList()),
                "Arrays.stream(new int[]{1, 2})",  // --> IntStream
                "" + Arrays.stream(new int[]{1, 2}).boxed().collect(Collectors.toList()),
                "Stream.of(managers)",
                "" + Stream.of(managers).collect(Collectors.toList()),
                "Arrays.stream(managers)",
                "" + Arrays.stream(managers).collect(Collectors.toList())));
    }

    /**
     * String --> char[]
     * (1) 方法1(不行): 使用{@link String#chars()}, 返回IntStream, 没有CharStream
     * (2) 方法2: {@link String#toCharArray()}
     */
    @Test
    public void stringStreamTest() {
        // 方法1
        char[] chars = "Veinthrough".toCharArray();
        // 方法2(不行): int[]不能转换成char[]
//        chars = "Veinthrough".chars().map(intValue -> (char) intValue).toArray();
        log.info(methodLog(Arrays.toString(chars)));
    }
}
