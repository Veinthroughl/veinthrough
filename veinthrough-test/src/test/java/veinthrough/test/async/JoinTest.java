package veinthrough.test.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class JoinTest {
    @Test
    public void joinTest() {
        Thread toJoin = Thread.currentThread();
        for (int i = 1; i <= 5; i++) {
            Thread curThread = new JoinThread(toJoin);
            curThread.start();
            toJoin = curThread;
        }
    }

    static class JoinThread extends Thread {
        private Thread thread;

        JoinThread(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            try {
                thread.join();
                log.info(methodLog((thread.getName() + " terminated.")));
            } catch (InterruptedException e) {
                log.warn(exceptionLog(e));
            }
        }
    }
}
