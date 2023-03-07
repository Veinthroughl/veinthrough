package veinthrough.test.concurrency_1.lock;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 文档：深入浅出Java Concurrency/《锁机制 part 6 CyclicBarrier》
 *
 * CyclicBarrier与CountDownLatch的区别:
 * 1. latch.countDown()/latch.await()配合使用,
 * 使用countDown()和使用await()的两种线程不是一样的，有点像生产者与消费者模型，
 * 实际实现: await()等待获取锁(acquireShared), countDown()递减为0时释放锁(releasedShared)
 * 也可以理解为调用countDown()的线程为发令员，调用await()的线程等待发令；
 * 发令员可以有多个，等待发令的线程也可以有多个，但是必须等待每个发令员全部发令完。
 * 2. CyclicBarrier
 * (1) 每个线程都是对称的
 * (2) 可以循环使用
 */
@Slf4j
public class CyclicBarrierTest {
    private final static int COUNT = 25;
    private final static int GROUP = 5;
    private final static int TIME_SLICE_SHORT = 10;
    private final static int TIME_SLICE_LONG = 100;
    private CyclicBarrier barrier = new CyclicBarrier(GROUP + 1);

    /**
     * (1) 将COUNT个线程分成若干组，每个组里面有GROUP个线程成员，所以会执行COUNT/GROUP轮；
     * 每个成员分别执行不同任务(sleep不同的时间，sleep根据i和TIME_SLICE_SHORT计算得到)，
     * 每个线程执行完调用await()必须等待组内的所有成员完成才能继续；
     * (2) 调用await()会返回组内剩余成员个数，
     * 所以最终所有成员完成：相当于await()返回0
     * (3) 这里新建barrier的时候使用了GROUP+1, 是为了把主线程也当作一员
     * (4) 这里为了使主线程最后一个完成，sleep的时间更长，使用了TIME_SLICE_LONG
     * (5) 主线程在每一轮最后等待其他成员执行完，一轮完成后重新新建线程执行下一轮
     */
    @Test
    public void test() {
        // 这里步骤1(主线程任务)必须在步骤2(组内线程任务)前，否则会产生死锁;
        // 也就是说要保证主线程为组内最后一个完成(然后cyclic barrier自动重置)
        // 比如COUNT=25，GROUP=5，每一组内有6(GROUP+1)个线程
        // i==5时：先创建了线程5(第6个线程)，也就是说这一轮已经完成，
        // 但是此时主线程还在await()并且不会再继续创建线程，没有其他的线程来继续完成这一轮。
        for (int i = 0; i <= COUNT; i++) {
            // 1. 主线程任务
            if (i > 0 && i % GROUP == 0)
                workAndWait(new CyclicTask(i, TIME_SLICE_LONG)); // 主线程更慢，wait
            // 2. 组内线程任务
            if(i<COUNT) threadWorkAndWait(new CyclicTask(i, TIME_SLICE_SHORT));  // 组内任务更快
        }
    }

    private void threadWorkAndWait(final Runnable work) {
        new Thread(() -> {
            work.run();
            finish();
        }).start();
    }

    private void workAndWait(final Runnable work) {
        work.run();
        finish();
    }

    private void finish() {
        try {
            // 第几个完成
            int finish = GROUP - barrier.await();
            log.info(methodLog(finish));
        } catch (InterruptedException | BrokenBarrierException ignored) {
        }
    }

    @AllArgsConstructor
    private static class CyclicTask implements Runnable {
        private int index;
        private int timeSlice;

        @Override
        public void run() {
            final int time = (GROUP - index % GROUP) * timeSlice;
            try {
                log.info(methodLog("Sleeping " + time));
                Thread.sleep(time);
            } catch (Exception ignored) {
            }
        }
    }
}
