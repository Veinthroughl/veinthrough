package veinthrough.test.concurrency_1.atomic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class ReorderingTest {

    private static int x = 0, y = 0, a = 0, b = 0;

    /**
     * 这个例子不太能说明指令重排: 即使出现(0,0), 也有可能是数据还没刷新到主内存。
     * 
     * one/two两个线程修改区x,y,a,b四个变量，在执行100次的情况下，可能得到(0 1)或者（1 0）或者（1 1）。
     * 事实上按照JVM的规范以及CPU的特性有很可能得到（0 0）。当然上面的代码大家不一定能得到（0 0），
     * 因为run()里面的操作过于简单，可能比启动一个线程花费的时间还少，因此上面的例子难以出现（0,0）。
     * 但是在现代CPU和JVM上确实是存在的。
     * (1) 由于run()里面的动作对于结果是无关的，因此里面的指令可能发生【指令重排序】，
     * (2) 即使是按照程序的顺序执行，数据变化刷新到主存也是需要时间的。
     * 假定是按照a=1;x=b;b=1;y=a;执行的，x=0是比较正常的，虽然a=1在y=a之前执行的，
     * 但是由于线程one执行a=1完成后还没有来得及将数据1写回主存（这时候数据是在线程one的堆栈里面的），
     * 线程two从主存中拿到的数据a可能仍然是0（显然是一个过期数据，但是是有可能的），
     * 这样就发生了数据错误(这里就涉及【Happens-before】了)。
     * 在两个线程交替执行的情况下数据的结果就不确定了，在机器压力大，多核CPU并发执行的情况下，数据的结果就更加不确定了。
     *
     * 某一次的结果:
     * (0,0): 0,
     * (0,1): 9458,
     * (1,0): 541,
     * (1,1): 1
     */
    @Test
    public void test() throws InterruptedException {
        AtomicInteger count00 = new AtomicInteger(0);
        AtomicInteger count01 = new AtomicInteger(0);
        AtomicInteger count10 = new AtomicInteger(0);
        AtomicInteger count11 = new AtomicInteger(0);
        for (int i = 0; i < 10000; i++) {
            x = y = a = b = 0;
            Thread one = new Thread(() -> {
                a = 1;
                x = b;
            });
            Thread two = new Thread(() -> {
                b = 1;
                y = a;
            });
            one.start();
            two.start();
            one.join();
            two.join();
            if (x == 0 && y == 0) count00.getAndIncrement();
            if (x == 0 && y == 1) count01.getAndIncrement();
            if (x == 1 && y == 0) count10.getAndIncrement();
            if (x == 1 && y == 1) count11.getAndIncrement();
        }
        log.info(methodLog(
                "(0,0)", "" + count00,
                "(0,1)", "" + count01,
                "(1,0)", "" + count10,
                "(1,1)", "" + count11));
    }
}
