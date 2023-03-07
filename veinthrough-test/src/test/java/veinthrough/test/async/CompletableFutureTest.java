package veinthrough.test.async;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.*;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static veinthrough.api.util.MethodLog.exceptionLog;

/**
 * {@link CompletionStage}表明执行到某一个阶段, 接下来可以执行下一个动作
 * {@link CompletableFuture}在CompletionStage的基础上实现了{@link Future}
 * 1. 创建CompletableFuture
 * 2. then, 下一步执行
 * 3. combine methodReferenceTest
 * 4. exception methodReferenceTest
 * 5. either methodReferenceTest [TODO]
 */
@Slf4j
public class CompletableFutureTest {
    private static final boolean WITH_EXCEPTION=true, NO_EXCEPTION=false;
    /**
     * 1. 创建CompletableFuture:
     * (1) {@link CompletableFuture#supplyAsync(Supplier)}, 将会返回CompletableFuture<U>, 返回U类型的运行结果
     * (2) {@link CompletableFuture#runAsync(Runnable)}, 将会返回CompletableFuture<Void>, 也就是只运行没有运行结果
     * 2. then, 下一步执行:
     * (1) 在之后执行一个function;
     * {@link CompletableFuture#thenApply(Function)}, 同步执行, 使用then之前的同一个线程
     * {@link CompletableFuture#thenApplyAsync(Function)}, 异步执行
     * {@link CompletableFuture#thenApplyAsync(Function, Executor)}, 异步执行，同时制定了线程池
     * Async结尾的方法都是可以异步执行的，如果指定了线程池，会在指定的线程池中执行，如果没有指定，默认会在ForkJoinPool.commonPool()中执行
     * (2) {@link CompletableFuture#thenAccept(Consumer)}, 这里的consumer没有返回值所以一般作为最后一步执行
     */
    @Test
    public void basicTest() {
        CompletableFuture.supplyAsync(this::getName)
                .thenApply(this::addPrefixToName)
                .thenAccept(this::log);
    }

    /**
     * 1. thenApply/thenCompose
     * {@link CompletableFuture#thenApply(Function)}, 这个Function为 Function<? super T,? extends U>
     * {@link CompletableFuture#thenCompose(Function)}, 这个Function为 Function<? super T, ? extends CompletionStage<U>>
     * thenCompose方法会在某个任务执行完成后，将该任务的执行结果作为方法入参然后执行指定的方法，该Function会返回一个新的CompletableFuture实例
     *
     * 2. thenCompose/thenCombine
     * {@link CompletableFuture#thenCompose(Function)}, 这个Function为 Function<? super T, ? extends CompletionStage<U>>
     * {@link CompletableFuture#thenCombine(CompletionStage, BiFunction)}, 这个BiFunction为 BiFunction<? super T,? super U,? extends V>
     */
    @Test
    public void combineTest() {
        CompletableFuture.supplyAsync(this::getName)
                .thenCompose(this::addPrefixToNameAsStage)
                .thenAccept(this::log);

        CompletableFuture.supplyAsync(this::getName)
                .thenCombine(supplyAsync(this::getPrefix), this::combineNameWithPrefix)
                .thenAccept(this::log);
    }

    /**
     * 1. exceptionally有点像catch的功能: {@link CompletableFuture#exceptionally(Function)}
     * * (1) 遇到exception会跳(跳过中间)到exceptionally执行
     * * (2) 只有一个参数(exception), 内部只能拿到exception的数据
     * * (3) 转换: Throwable --> T(和原来的返回相同类型)
     * 2. handle: {@link CompletableFuture#handle(BiFunction)}, BiFunction<? super T, Throwable, ? extends U>
     * (1) 只是针对【上一步】(不像exceptionally会跳过)可能产生异常
     * (2) 有两个参数(T, exception), 内部能拿到((T,null)无异常时)/((null,exception)异常时)的数据
     * (3) 转换: (T,exception) --> T(和原来的返回相同类型)
     * 3. whenComplete: {@link CompletableFuture#whenComplete(BiConsumer)}, 作为一个消费者不返回值
     * (1) 只是针对【上一步】(不像exceptionally会跳过)可能产生异常
     * (2) 有两个参数(T, exception), 内部能拿到((T,null)无异常时)/((null,exception)异常时)的数据
     * (3) 转换: (T,exception) --> Void(和原来的返回相同类型), 作为一个消费者不返回值，所以一般放在执行流的最后
     * 4. 没有异常处理, 因为是async, 线程将死掉
     * 5. 一般情况下不同时使用exceptionally/handle/whenComplete/不要同时用
     */
    @Test
    public void exceptionTest() {
        exceptionallyTest();
        whenCompleteTest();
        handleTest();
        ignoreExceptionTest();
    }

    /**
     * 1. exceptionally有点像catch的功能: {@link CompletableFuture#exceptionally(Function)}
     * (1) 遇到exception会跳(跳过中间)到exceptionally执行
     * (2) 只有一个参数(exception), 内部只能拿到exception的数据
     * (3) 转换: Throwable --> T(和原来的返回相同类型)
     */
    @Test
    public void exceptionallyTest() {
        Stream.of(WITH_EXCEPTION, NO_EXCEPTION)
                .forEach(
                        exception -> supplyAsync(() -> getName(exception))
                                .exceptionally(this::exceptionHandler)
                                .thenApply(this::addPrefixToName) // [Hello][Exception], 或者[Hello]Veinthrough
                                .thenAccept(this::log));
        Stream.of(WITH_EXCEPTION, NO_EXCEPTION)
                .forEach(
                        exception -> supplyAsync(() -> getName(exception))
                                .thenApply(this::addPrefixToName) // 这里遇到exception会跳过thenApply的执行
                                .exceptionally(this::exceptionHandler) // [Exception], 或者[Hello]Veinthrough
                                .thenAccept(this::log));
    }

    /**
     * 2. handle: {@link CompletableFuture#handle(BiFunction)}, BiFunction<? super T, Throwable, ? extends U>
     * (1) 只是针对【上一步】(不像exceptionally会跳过)可能产生异常, 【我觉得还是会跳过】
     * (2) 有两个参数(T, exception), 内部能拿到((T,null)无异常时)/((null,exception)异常时)的数据
     * (3) 转换: (T,exception) --> T(和原来的返回相同类型)
     */
    @Test
    public void handleTest() {
        Stream.of(true, false)
                .forEach(
                        exception -> supplyAsync(() -> getName(exception))
                                .handle(this::handler) // (T,exception) --> T
                                .thenAccept(this::log));
    }

    /**
     * 3. whenComplete: {@link CompletableFuture#whenComplete(BiConsumer)}, 作为一个消费者不返回值
     * (1) 只是针对【上一步】(不像exceptionally会跳过)可能产生异常, 【我觉得还是会跳过】
     * (2) 有两个参数(T, exception), 内部能拿到((T,null)无异常时)/((null,exception)异常时)的数据
     * (3) 转换: (T,exception) --> Void(和原来的返回相同类型), 作为一个消费者不返回值，所以一般放在执行流的最后
     */
    @Test
    public void whenCompleteTest() {
        Stream.of(true, false)
                .forEach(
                        exception -> supplyAsync(() -> getName(exception))
                                .thenApply(this::addPrefixToName)
                                .whenComplete(this::completeHandler));
    }

    /**
     * 4. 没有异常处理, 因为是async, 线程将死掉
     */
    @Test
    public void ignoreExceptionTest() {
        Stream.of(true, false)
                .forEach(
                        exception -> supplyAsync(() -> getName(exception))
                                .thenApply(this::addPrefixToName)
//                                .exceptionally(this::exceptionHandler) // 这里已经没有exception, 所以handle后面不需要exceptionally
                                .thenAccept(this::log));
    }

    private String getName() {
        return "Veinthrough";
    }

    private String getName(boolean exception) {
        if (exception)
            throw new IllegalArgumentException();
        return getName();
    }

    private String getPrefix() {
        return "[Hello]";
    }

    private String addPrefixToName(String name) {
        return getPrefix() + name;
    }

    private CompletableFuture<String> addPrefixToNameAsStage(String name) {
        return CompletableFuture.supplyAsync(() -> addPrefixToName(name));
    }

    private String combineNameWithPrefix(String name, String prefix) {
        return prefix + name;
    }

    private String handler(Object o, Throwable e) {
        if (e != null) log.warn(exceptionLog(e));
        return e == null ? addPrefixToName(o.toString()) : "[Exception]";
    }

    private void completeHandler(Object o, Throwable e) {
        log(handler(o, e));
    }

    private String exceptionHandler(Throwable e) {
        log.warn(exceptionLog(e));
        return "[Exception]";
    }

    private void log(String str) {
        log.info(str);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn(exceptionLog(e));
        }
    }
}