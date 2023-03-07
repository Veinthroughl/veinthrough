package veinthrough.test.async;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 1. Future/FutureTask/Runnable/Callable的使用:
 * {@link WordCounter#byFutureTask()}/{@link WordCounter#byThreadPool()}
 * 2. Handle exception in Stream: {@link veinthrough.test.exception.StreamExceptionTest}
 *
 * 3. APIs:
 * (1) FutureTask实际上为Runnable和Future的结合体, 有点像Callable
 * FutureTask<V> implements RunnableFuture<V>
 * RunnableFuture<V> extends Runnable, Future<V>
 * (2) Runnable/Callable, [Problem] TODO
 * 调用{@link Callable#call()}会抛出异常
 * 调用{@link Runnable#run()}不会抛出异常
 * (3) Callable --> FutureTask, 有点 Callable --> Runnable的意思
 * {@link FutureTask#FutureTask(Callable)}
 * {@link FutureTask#FutureTask(Runnable, Object)}
 * (4) Runnable --> Callable
 * {@link Executors#callable(Runnable)}
 * {@link Executors#callable(Runnable, Object)}
 * (5) 新建Thread使用的是Runnable
 * {@link Thread#Thread(Runnable)}
 * (6) 线程池可以submit Runnable/Callable, 但是invokeAll只能是Collection<? extends Callable<T>>
 * {@link ExecutorService#submit(Runnable)}, 返回{@link Future}
 * {@link ExecutorService#submit(Callable)}
 * {@link ExecutorService#submit(Runnable, Object)}
 * 【阻塞】{@link ExecutorService#invokeAll(Collection)}, 注意这里是Collection<? extends Callable<T>>
 */
@SuppressWarnings("unused")
@Slf4j
class FutureTaskTest {
}
