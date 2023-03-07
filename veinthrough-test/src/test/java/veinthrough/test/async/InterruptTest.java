package veinthrough.test.async;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.async.LoopRunnable;

import java.util.Collection;
import java.util.concurrent.*;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.JdkFutureAdapters.listenInPoolThread;
import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * APIs:
 * 1. 使用LoopCallable/LoopRunnable循环执行一个任务(count), 直到condition/timeout/interrupted结束,
 * 结束时还能执行一个结束任务: {@link LoopRunnable}/{@link veinthrough.api.async.LoopCallable}
 * 2. 使用guava中的{@link Futures#addCallback(ListenableFuture, FutureCallback)}来添加callback
 *
 * Tests:
 * pool中有hyperTask/sleepyTask, 实现在一段时间之后完全关闭pool
 * 1. terminateTask: 通过在LoopRunnable中增加finish参数来执行结束操作
 * 2. emptyTask+callback: 给emptyTask添加一个执行结束操作的callback
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class InterruptTest {
    private static final int INTERVAL = MILLIS_PER_SECOND;
    private static final int THRESHOLD = 10;
    private static final int TIMEOUT = THRESHOLD * INTERVAL;
    private static ExecutorService pool;

    private int hyperCounter, sleepyCounter, terminateCounter, emptyCounter;
    private final Callable<Object> hyperTask, sleepyTask, terminateTask, emptyTask;

    {
        hyperCounter = 0;
        sleepyCounter = 0;
        terminateCounter = 0;
        // 不睡眠/数到THRESHOLD停止
        hyperTask = Executors.callable(
                LoopRunnable.hyper(
                        () -> {
                            log.info(methodLog(hyperCounter,"hyperTask"));
                            hyperCounter++;
                        },
                        () -> hyperCounter>=THRESHOLD),
                null);
        // 循环睡眠(间隔INTERVAL)/TIMEOUT停止
        sleepyTask = Executors.callable(
                LoopRunnable.sleepyAtInterval(
                        () -> {
                            log.info(methodLog(sleepyCounter, "sleepyTask"));
                            sleepyCounter++;
                        },
                        TIMEOUT,
                        INTERVAL),
                null);
        // 循环睡眠(间隔INTERVAL/2)/TIMEOUT/2停止，早于sleepyTask结束
        // 停止后调用pool.shutdownNow, 通过在LoopRunnable中增加finish参数来执行结束操作
        terminateTask = Executors.callable(
                LoopRunnable.sleepyAtInterval(
                        // task
                        () -> {
                            log.info(methodLog(terminateCounter, "terminateTask"));
                            terminateCounter++;
                        },
                        // half time
                        TIMEOUT / 2,
                        INTERVAL / 2,
                        // finish
                        () -> {
                            log.info(methodLog(
                                    "Starting to stop all actively executing tasks ..."));
                            pool.shutdownNow();
                        }));
        // 循环睡眠(间隔INTERVAL/2)/TIMEOUT/2停止，早于sleepyTask结束
        // 停止后无操作, 但是可以通过添加callback来执行结束操作
        emptyTask = Executors.callable(
                LoopRunnable.sleepyAtInterval(
                        () -> {
                            log.info(methodLog(emptyCounter, "emptyTask"));
                            emptyCounter++;
                        },
                        // half time
                        TIMEOUT / 2,
                        INTERVAL / 2),
                null);
    }

    /**
     * 1. terminateTask: 通过在LoopRunnable中增加finish参数来执行结束操作
     * (1) 注意{@link ExecutorService#invokeAll(Collection)}为阻塞函数, 直到所有任务退出。
     */
    @Test
    public void terminateByLoopRunnable() throws InterruptedException {
        pool = Executors.newCachedThreadPool();
        pool.invokeAll(ImmutableList.of(hyperTask, sleepyTask, terminateTask));
        pool.shutdownNow();
        pool = null;
    }

    /**
     * 2. emptyTask+callback: 给emptyTask添加一个执行结束操作的callback
     * (1) emptyTask的执行: new Thread(emptyTask).start()
     * (2) 注意使用正确的future: thread还没执行就可以获取到future,
     * new Thread(futureTask).start()和futureTask会互相绑定
     * (3) thread.start()/addCallback()的顺序无所谓: 可以先addCallback()再start()
     * (4) {@link JdkFutureAdapters#listenInPoolThread(Future)}使用的的线程: ListenableFutureAdapter-thread-0
     */
    @Test
    public void terminateByCallback() throws InterruptedException {
        hyperCounter=0; sleepyCounter=0; emptyCounter=0;
        pool = Executors.newCachedThreadPool();
        final FutureTask<Object> emptyFuture = new FutureTask<>(emptyTask);
        // 1. add callback
        addCallback(
                // 使用正确的future: new Thread(futureTask).start和futureTask会互相绑定
                listenInPoolThread(emptyFuture),
                new FutureCallback<Object>() {
                    @Override
                    public void onFailure(Throwable e) {
                        log.error(exceptionLog(e));
                    }

                    @Override
                    public void onSuccess(Object value) {
                        log.info(methodLog("Starting to stop all actively executing tasks ..."));
                        pool.shutdownNow();
                    }
                });
        // 2. start emptyTask
        // thread.start()/addCallback()的顺序无所谓: 可以先addCallback()再start()
        new Thread(emptyFuture).start();

        // 3. start hyperTask/sleepyTask
        pool.invokeAll(ImmutableList.of(hyperTask, sleepyTask));
        pool.shutdown();
        pool = null;
    }

    /**
     * 2. emptyTask+callback: 给emptyTask添加一个执行结束操作的callback
     * (1) emptyTask的执行: pool.submit(emptyTask)
     * 【注意】这里不能使用{@link ExecutorService#invokeAll(Collection)},
     * 因为invokeAll为阻塞函数, 直到所有任务退出, 而emptyTask的future只有invokeAll完成才能得到;
     * 如果使用invokeAll，得到emptyTask的时候hyperTask/sleepyTask已经执行完。
     * (2) 使用正确的future: 这里的future必须通过submit得到,
     * 不能像new Thread(emptyTask).start()那样线程还没执行就已经有future。
     * (3) {@link JdkFutureAdapters#listenInPoolThread(Future)}使用的的线程: ListenableFutureAdapter-thread-0
     * (4) 这里main线程必须等待, 否则main线程退出其他线程直接退出
     */
    @Test
    public void terminateByCallback2() throws InterruptedException {
        hyperCounter=0; sleepyCounter=0; emptyCounter=0;
        pool = Executors.newCachedThreadPool();

        // 1. start hyperTask/sleepyTask
        pool.submit(hyperTask);
        pool.submit(sleepyTask);
        // 2. start emptyTask
        Future<Object> future = pool.submit(emptyTask);
        // 3. add callback
        addCallback(
                // 使用正确的future: 这里的future必须通过submit得到
                listenInPoolThread(future),
                new FutureCallback<Object>() {
                    @Override
                    public void onFailure(Throwable e) {
                        log.error(exceptionLog(e));
                    }

                    @Override
                    public void onSuccess(Object value) {
                        log.info(methodLog("Starting to stop all actively executing tasks ..."));
                        pool.shutdownNow();
                    }
                });
        // 4. 这里main线程必须等待, 否则main线程退出其他线程直接退出
        pool.awaitTermination(30, TimeUnit.SECONDS);
        pool.shutdown();
        pool = null;
    }
}
