package veinthrough.test.async;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.DoublePredicate;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * 
 * 1. Calculate the rate of double numbers larger than 0.5 in 0...1 by conduct a 1000000 random methodReferenceTest.
 * 2. {@link WordCounter#byForkJoin()}
 */
@Slf4j
public class ForkJoinTest {
    @Test
    public void NumberCountTest() {
        final int SIZE = 1000000;
        double[] numbers = new double[SIZE];
        for (int i = 0; i < SIZE; i++) numbers[i] = Math.random();

        NumberCounter counter = new NumberCounter(numbers, 0, numbers.length,
                        number -> number > 0.5);
        new ForkJoinPool().invoke(counter);
        log.info(methodLog(
                "Total(0<=n<1)", "" + SIZE,
                "Counts of >0.5", "" + counter.join(),
                "Rate of >0.5", "" + (double) counter.join() / numbers.length));
    }

    @AllArgsConstructor
    class NumberCounter extends RecursiveTask<Integer> {
        private static final int THRESHOLD = 1000; // 大于1000个使用多线程
        private double[] values;
        private int from;
        private int to;
        private DoublePredicate filter;

        @Override
        protected Integer compute() {
            // 数量<=THRESHOLD: 单线程
            if (to - from <= THRESHOLD) {
                int count = 0;
                for (int i = from; i < to; i++) {
                    if (filter.test(values[i])) count++;
                }
                return count;
            }
            // 数量>THRESHOLD: 递归多线程
            else {
                int mid = (from + to) / 2;
                NumberCounter first = new NumberCounter(values, from, mid, filter);
                NumberCounter second = new NumberCounter(values, mid, to, filter);
                invokeAll(first, second);
                return first.join() + second.join();
            }
        }
    }

}
