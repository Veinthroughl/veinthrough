package veinthrough.test.concurrency_1.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. park()的伪代码
 * 只要permit为1或者中断状态为true，那么执行park就不能够阻塞线程。park只可能消耗掉permit，但不会去消耗掉中断状态。
 * # park() {
 * #     if(permit > 0) {
 * #         permit = 0;
 * #         return;
 * #     }
 *
 * #     if(中断状态 == true) {
 * #         return;
 * #     }
 *
 * #     阻塞当前线程;  // 将来会从这里被唤醒
 *
 * #     if(permit > 0) {
 * #         permit = 0;
 * #     }
 * # }
 *
 * 2. unpark()的伪代码
 * unpark一定会将permit置为1，如果线程阻塞，再将其唤醒。从实现可见，无论调用几次unpark，permit只能为1。
 * # unpark(Thread thread) {
 * #     if (permit < 1) {
 * #         permit = 1;
 * #         if (thread处于阻塞状态)
 * #             唤醒线程thread;
 * #     }
 * # }
 *
 * 3. interrupt()的伪代码
 * (1) interrupt()会设置中断状态为true。
 * (2) interrupt()还会去调用unpark()，所以也会把permit置为1的。
 * # interrupt(){
 * #     if(中断状态 == false) {
 * #         中断状态 = true;
 * #     }
 * #     unpark(this);    // 注意这是Thread的成员方法，所以我们可以通过this获得Thread对象
 * # }
 *
 * 4. sleep()的伪代码
 * sleep()会去检测中断状态，如果检测到了，那就消耗掉中断状态后，抛出中断异常。但sleep()不会去动permit。
 * sleep() / wait() / join()调用后一定会消耗掉中断状态，无论interrupt()操作先做还是后做。
 * # sleep(){ // 这里我忽略了参数，假设参数是大于0的即可
 * #     if(中断状态 == true) {
 * #         中断状态 = false;
 * #         throw new InterruptedException();
 * # }
 *
 * #     线程开始睡觉;
 *
 * #     if(中断状态 == true) {
 * #         中断状态 = false;
 * #         throw new InterruptedException();
 * #     }
 * # }
 */
@Slf4j
public class ParkTest {
    @Test
    public void parkTest1() {
        LockSupport.park();  // 因为此时permit为0且中断状态为false，所以阻塞
    }

    @Test
    public void parkTest2() {
        LockSupport.unpark(Thread.currentThread());  // 置permit为1
        LockSupport.park();  // 消耗掉permit后，直接返回了
    }

    @Test
    public void parkTest3() {
        LockSupport.unpark(Thread.currentThread());
        LockSupport.park();  // 消耗掉permit后，遇到中断直接返回了(并不会消耗中断状态)
        LockSupport.park();  // 此时permit为0，中断状态为false，必然会阻塞
    }

    @Test
    public void interruptTest1() {
        Thread.currentThread().interrupt();
        LockSupport.park();  // 消耗掉permit后，直接返回了
    }

    @Test
    public void interruptTest2() {
        Thread.currentThread().interrupt();
        LockSupport.park();  // 消耗掉permit后，遇到中断直接返回了(并不会消耗中断状态)
        LockSupport.park();  // 无permit可消耗，但遇到中断直接返回了(并不会消耗中断状态)
        LockSupport.park();  // 同上
    }

    @Test
    public void sleepTest1() {
        Thread.currentThread().interrupt(); // interrupt(): (1) 设置中断状态为true (2) 调用unpark()置permit为1
        try {
            Thread.sleep(1000);  // sleep(): (1) 消耗掉中断状态 (2) 不会消耗permit
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sleepTest2() {
        Thread.currentThread().interrupt(); // interrupt(): (1) 设置中断状态为true (2) 调用unpark()置permit为1
        try {
            Thread.sleep(1000); // 消耗掉中断状态, wait()/join()效果同sleep()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LockSupport.park();  // 消耗掉permit
        LockSupport.park();  // 因为此时permit为0且中断状态为false，所以阻塞
    }

    @Test
    public void parkAndInterruptTest() throws InterruptedException {
        Thread thread = new Thread(new ParkRunnable());
        thread.start();
        log.info(methodLog(0, "Main sleeping..."));
        Thread.sleep(1000);
        log.info(methodLog(1, "Main interrupting..."));
        thread.interrupt();
        log.info(methodLog(2, "Main sleeping..."));
        Thread.sleep(1000);
        log.info(methodLog(3, "Main interrupting..."));
        thread.interrupt();
        Thread.sleep(1000);
        log.info(methodLog(4, "Main ending..."));

    }

    public static class ParkRunnable implements Runnable {
        @Override
        public void run() {
            log.info(methodLog(0, "Thread starting..."));
            log.info(methodLog(1, "Thread parking..."));
            LockSupport.park();
            log.info(methodLog(2, "Thread doing..."));
            log.info(methodLog(3, "Thread parking..."));
            LockSupport.park();
            log.info(methodLog(4, "Thread ending..."));
        }
    }

}
