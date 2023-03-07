package veinthrough.test.guava;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.async.InterruptTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.util.concurrent.Futures.*;
import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * APIs:
 * 1. get a {@link ListenableFuture}:
 * (1) [static][sync] {@link Futures#immediateFuture(Object value)}/{@link Futures#immediateFailedCheckedFuture(Exception)}:
 * ListenableFuture必须用线程来实现才是async,否则就是sync
 * (2) [static][async] {@link JdkFutureAdapters#listenInPoolThread(Future)},
 * Convert a Future to a ListenableFuture
 * (3) [async] ListeningExecutorService.submit(Callable/Runnable)
 * 2. get a {@link ListeningExecutorService}:
 * {@link MoreExecutors#listeningDecorator(ExecutorService)}: decorate ExecutorService as ListeningExecutorService.
 * 3. {@link FutureCallback}: FutureCallback<T>
 * (1) onFailure(Throwable e)
 * (2) onSuccess(T value)
 * 4. Futures.addCallback(ListenableFuture<T>, FutureCallback<T>)
 *   
 * Tests:
 * Run a count task, and cancel it after a while:
 * 1. Use {@link Futures#immediateFuture(Object)}will lead to sync manner, that is,
 * 在添加callback的时候已经执行完了。
 * 2. Use {@link JdkFutureAdapters#listenInPoolThread(Future)}
 * 3. Failed with a exception: {@link Futures#immediateFailedFuture(Throwable)}
 * 4. Failed with a exception: run a async task throwing a exception.
 * 5. Other tests:
 * {@link InterruptTest#terminateByCallback()},
 * {@link InterruptTest#terminateByCallback2()}.
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class ListenableFutureTest {
    private static final int THRESHOLD = 10;
    private static int i, j;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final ListeningExecutorService listenablePool =
            MoreExecutors.listeningDecorator(pool);

    /**
     * Run a count task, and cancel it after a while:
     * 1. Use {@link Futures#immediateFuture(Object)}will lead to sync manner, that is,
     * 在添加callback的时候已经执行完了。
     */
    @Test
    public void syncCancelTest() {
        // 这里直接在main中执行
        ListenableFuture<Integer> future =
                immediateFuture(count()); // 直接执行count()

        addCallback(future, callbackForCount());
        waitForCountBySleep();
        log.info(methodLog("cancel", "" + future.cancel(true)));
    }

    /**
     * Run a count task, and cancel it after a while:
     * 2. Use {@link JdkFutureAdapters#listenInPoolThread(Future)}
     */
    @Test
    public void asyncCancelTest() {
        ListenableFuture<Integer> future =
                JdkFutureAdapters.listenInPoolThread(
                        pool.submit(this::count));
        pool.shutdown();

        addCallback(future, callbackForCount());
        waitForCountBySleep();
        log.info(methodLog("cancel", "" + future.cancel(true)));
    }

    /**
     * Run a count task, and cancel it after a while:
     * 3. Failed with a exception: {@link Futures#immediateFailedFuture(Throwable)}
     */
    @Test
    public void syncExceptionTest() {
        ListenableFuture<Integer> future =
                immediateFailedFuture(new ZeroDivisorException());

        addCallback(future, callbackForCount());
    }

    /**
     * Run a count task, and cancel it after a while:
     * 4. Failed with a exception: run a async task throwing a exception.
     */
    @Test
    public void asyncExceptionTest() {
        ListenableFuture<Integer> future =
                listenablePool.submit(this::divide);
        listenablePool.shutdown();

        addCallback(future, callbackForCount());
    }

    private Integer count() {
        try {
            for (i = 0; i < THRESHOLD; i++) {
                log.info(methodLog(i));
                Thread.sleep(MILLIS_PER_SECOND);
            }
        } catch (InterruptedException e) {
            log.info(exceptionLog(e));
        }
        return i;
    }

    private FutureCallback<Integer> callbackForCount() {
        return new FutureCallback<Integer>() {
            @Override
            public void onFailure(Throwable e) {
                log.info(methodLog(
                        "Failed with " + j
                                + " for " + e.getClass().getSimpleName()));
            }
            @Override
            public void onSuccess(Integer value) {
                log.info(methodLog("Succeeded with " + value));
            }
        };
    }

    private void waitForCountBySleep() {
        try {
            for (j = 0; j < THRESHOLD / 2; j++) {
                log.info(methodLog(j));
                Thread.sleep(MILLIS_PER_SECOND);
            }
        } catch (InterruptedException e) {
            log.info(exceptionLog(e));
        }
    }

    private int divide() throws ZeroDivisorException {
        // convert a runtime-exception to a checked-exception
        throw new ZeroDivisorException();
    }

    private static class ZeroDivisorException extends Exception {
        ZeroDivisorException() {
            this("the divisor can't be 0");
        }

        ZeroDivisorException(String message) {
            super(message);
        }
    }
}
