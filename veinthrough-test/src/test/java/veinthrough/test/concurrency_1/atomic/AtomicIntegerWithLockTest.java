package veinthrough.test.concurrency_1.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * ReentrantLock和synchronized的性能比较
 */
@Slf4j
public class AtomicIntegerWithLockTest {
    private static final int MAX_THREADS = 10;
    private static final int TEST_COUNT = 10;
    private static final int LOOP_COUNT = 1000;
    @SuppressWarnings("unused")
    private static int staticValue;

    @Test
    public void test() throws InterruptedException {
        long cost1 = 0, cost2 = 0;
        long start, end;
        for (int i = 0; i < TEST_COUNT; i++) {
            start = System.nanoTime();
            AtomicIntegerWithLock value = new AtomicIntegerWithLock(0);
            Thread[] ts = new Thread[MAX_THREADS];
            for (int j = 0; j < MAX_THREADS; j++) {
                ts[j] = new Thread(() -> {
                    for (int k = 0; k < LOOP_COUNT; k++) {
                        value.incrementAndGet(); // 使用ReentrantLock实现
                    }
                });
            }
            for (Thread t : ts) {
                t.start();
            }
            for (Thread t : ts) {
                t.join();
            }
            end = System.nanoTime();
            cost1 += (end - start);
        }
        cost1 /= TEST_COUNT;

        final Object lock = new Object();
        for (int i = 0; i < TEST_COUNT; i++) {
            staticValue = 0;
            start = System.nanoTime();
            Thread[] ts = new Thread[MAX_THREADS];
            for (int j = 0; j < MAX_THREADS; j++) {
                ts[j] = new Thread(() -> {
                    for (int k = 0; k < LOOP_COUNT; k++) {
                        synchronized (lock) { // 使用synchronized实现
                            ++staticValue;
                        }
                    }
                });
            }
            for (Thread t : ts) {
                t.start();
            }
            for (Thread t : ts) {
                t.join();
            }
            end = System.nanoTime();
            cost2 += (end - start);
        }
        cost2 /= TEST_COUNT;

        log.info(methodLog(
                "cost1", "" + cost1,
                "cost2", "" + cost2));
    }

    @SuppressWarnings("unused")
    public class AtomicIntegerWithLock {
        private int value;

        private Lock lock = new ReentrantLock();

        AtomicIntegerWithLock(int value) {
            this.value = value;
        }

        public final int get() {
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }

        public final void set(int newValue) {
            lock.lock();
            try {
                value = newValue;
            } finally {
                lock.unlock();
            }
        }

        public final int getAndSet(int newValue) {
            lock.lock();
            try {
                int ret = value;
                value = newValue;
                return ret;
            } finally {
                lock.unlock();
            }
        }

        public final boolean compareAndSet(int expect, int update) {
            lock.lock();
            try {
                if (value == expect) {
                    value = update;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public final int getAndIncrement() {
            lock.lock();
            try {
                return value++;
            } finally {
                lock.unlock();
            }
        }

        public final int getAndDecrement() {
            lock.lock();
            try {
                return value--;
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("UnusedReturnValue")
        final int incrementAndGet() {
            lock.lock();
            try {
                return ++value;
            } finally {
                lock.unlock();
            }
        }

        public final int decrementAndGet() {
            lock.lock();
            try {
                return --value;
            } finally {
                lock.unlock();
            }
        }

        public String toString() {
            return Integer.toString(get());
        }
    }
}
