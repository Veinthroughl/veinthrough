package veinthrough.test.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 *  1. 当一个Java应用，只有守护线程的时候，虚拟机就会自然退出。
 *  2. 守护线程在退出的时候并【不一定】会执行finally块中的代码，
 *  所以将释放资源等操作不要放在finally块中执行，这种操作是不安全的
 */
@Slf4j
public class DaemonTest {
    @Test
    public void daemonTest(){
        Thread daemonThread = new Thread(() -> {
            while (true) {
                try {
                    log.info(methodLog("i am alive"));
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                // 守护线程在退出的时候并不会一定执行finally块中的代码，
                // 所以将释放资源等操作不要放在finally块中执行，这种操作是不安全的
                } finally {
                    log.info(methodLog("finally block"));
                }
            }
        });
        // 当一个Java应用，只有守护线程的时候，虚拟机就会自然退出。
        daemonThread.setDaemon(true);
        daemonThread.start();
        // 确保main线程结束前能给daemonThread能够分到时间片
        try {
            Thread.sleep(1600);
        } catch (InterruptedException e) {
            log.warn(exceptionLog(e));
        }
    }
}