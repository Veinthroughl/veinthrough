package veinthrough.test.byte_code;

import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unused")
public class SynchronizedTest {
    private static int i = 0;
    private static ReentrantLock lock = new ReentrantLock();

    public synchronized static void test() {
        synchronizedMethod();
    }

    private synchronized static void synchronizedMethod() {
        i++;
    }

    private static void lockMethod() {
        try {
            lock.lock();
            i++;
        } finally {
            lock.unlock();
        }
    }

}
