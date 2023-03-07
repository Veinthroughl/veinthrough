package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.math.Math;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static veinthrough.api.math.Math.swap;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @see veinthrough.api.math.Math;
 */
@Slf4j
public class MathTest {
    @Test
    public void testMin2Power() {
        // noinspection deprecation
        Stream.of(2015, 100, 16, 71304193)
                .forEach(num ->
                        Stream.<Function<Integer, Integer>>of(
                                Math::minGreater2Power, Math::minGreater2Power2)
                                .forEach(min2Power ->
                                        log.info(methodLog("" + min2Power.apply(num)))));
    }

    @Test
    public void testSwap() {
        log.info(methodLog(
                Arrays.toString(swap(new int[]{5, 105}))));
    }
}
