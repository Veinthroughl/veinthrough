package veinthrough.test.concurrency_1.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. latch.countDown()/latch.await()配合使用,
 * 使用countDown()和使用await()的两种线程不是一样的，有点像生产者与消费者模型，
 * 实际实现: await()等待获取锁(acquireShared), countDown()递减为0时释放锁(releasedShared)
 * 也可用理解为调用countDown()的线程为发令员，调用await()的线程等待发令；
 * 2. 发令员可以有多个，等待发令的线程也可以有多个，但是必须等待每个发令员全部发令完。
 * 非对称, 可以实现:
 * (1) 1个发令(countDown), N个等待(await): 参数为1;
 * (2) M个发令(countDown), 1个等待(await): 参数为M
 * (3) M个发令(countDown), N个等待(await): 参数为M
 */
@Slf4j
public class CountDownLatchTest {
    private static final int TIMES = 10;

    @Test
    public void test() throws InterruptedException {
        timecost(
                () -> log.info(methodLog(Thread.currentThread().getName() + " Start.")));
    }

    private void timecost(final Runnable task) throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch overLatch = new CountDownLatch(TIMES);
        for (int i = 0; i < TIMES; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();                 // 所有线程一起等待开始
                    task.run();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    overLatch.countDown();              // 单个线程标记结束
                }
            }).start();
        }

        startLatch.countDown();                         // 发令枪, 所有进程开始
        overLatch.await();                              // 等待所有线程结束
    }
}
