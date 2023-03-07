package veinthrough.test.concurrency_1.atomic;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AtomicIntegerFieldUpdaterTest {
    @Test
    public void test() {
        DemoData data = new DemoData();
        assertEquals(getUpdater("value1").getAndSet(data, 10), 1);
        assertEquals(getUpdater("value2").incrementAndGet(data), 3);
        assertEquals(getUpdater("value3").decrementAndGet(data), 2);
        // java.lang.RuntimeException: java.lang.IllegalAccessException, 不能访问private
        //noinspection TestFailedLine
        assertTrue(getUpdater("value4").compareAndSet(data, 4, 5));
    }

    @SuppressWarnings("unused")
    class DemoData {
        public volatile int value1 = 1;
        volatile int value2 = 2;
        protected volatile int value3 = 3;
        private volatile int value4 = 4;
    }

    private AtomicIntegerFieldUpdater<DemoData> getUpdater(String fieldName) {
        return AtomicIntegerFieldUpdater.newUpdater(DemoData.class, fieldName);
    }

}
