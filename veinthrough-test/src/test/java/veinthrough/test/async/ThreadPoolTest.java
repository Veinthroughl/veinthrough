package veinthrough.test.async;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * APIs:
 * 1. [非阻塞]{@link ExecutorService#shutdown()}:
 * (1) 停止接收新的submit的任务；
 * (2) 已经提交的任务（包括正在跑的和队列中等待的）,会继续执行完成；
 * (3) 等到第(2)步完成后，才真正停止；
 * 2. [阻塞]{@link ExecutorService#shutdownNow()}:
 * (1) 跟 shutdown() 一样，先停止接收新submit的任务；
 * (2) 忽略队列里等待的任务；
 * (3) 尝试将正在执行的任务interrupt中断；
 * (4) 返回未执行的任务列表；
 * 【注意】它试图终止线程的方法是通过调用 Thread.interrupt() 方法来实现的，
 * 这种方法的作用有限，如果线程中没有sleep 、wait、Condition、定时锁等应用,
 * interrupt() 方法是无法中断当前的线程的。所以，shutdownNow() 并不代表线程池就一定立即就能退出，
 * 它也可能必须要等待所有正在执行的任务都执行完成了才能退出。但是大多数时候是能立即退出的。
 * 3. [阻塞]{@link ExecutorService#awaitTermination(long, TimeUnit)}:
 * 【注意】awaitTermination() 后，可以继续提交任务, 当前线程阻塞，直到:
 * (1) 等所有已提交的任务（包括正在跑的和队列中等待的）执行完；
 * (2) 或者等超时时间到了（timeout 和 TimeUnit设定的时间）；
 * (3) 或者线程被中断，抛出InterruptedException
 * (4) 然后会监测 ExecutorService 是否已经关闭，返回true（shutdown请求后所有任务执行完毕）或false（已超时）
 * 4. 关闭功能 【从强到弱】 依次是：shutdownNow() > shutdown() > awaitTermination()
 * 5. [非阻塞]{@link ExecutorService#submit(Runnable, Object)}
 * 6. 【注意】[阻塞]{@link ExecutorService#invokeAll(Collection)}为阻塞函数, 直到所有任务退出。
 * 
 * Tests:
 * {@link WordCounter#byThreadPool()}
 * {@link InterruptTest}
 * {@link veinthrough.test.guava.ListenableFutureTest}
 * {@link veinthrough.test.io.pipe.Piper}
 * {@link veinthrough.test.io.pipe.PipeTest}
 */
@SuppressWarnings("unused")
class ThreadPoolTest {
}
